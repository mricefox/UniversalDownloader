package com.mricefox.mfdownloader.lib;

import android.util.Pair;

import com.mricefox.mfdownloader.lib.assist.MFLog;
import com.mricefox.mfdownloader.lib.operator.BlockDownloadListener;
import com.mricefox.mfdownloader.lib.operator.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.operator.DownloadOperator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:executor for download
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private ExecutorService downloadExecutor;
    //    private BlockingQueue downloadQueue;
    private DownloadOperator downloadOperator;
    private final ConcurrentHashMap<Long, Download> runningDownloads;
    private Contract contract;
    private ConcurrentHashMap<Long, CountDownLatch> downloadOnStopLocks;
    private AtomicInteger maxDownloadCount;
    private boolean autoStartPending;
    private ProgressMonitor progressMonitor;

    DownloadConsumerExecutor(DownloadOperator downloadOperator, Contract contract,
                             int maxDownloadCount, boolean autoStartPending, long monitorCallbackPeriod) {
        this.maxDownloadCount = new AtomicInteger(maxDownloadCount);
        this.downloadOperator = downloadOperator;
        this.autoStartPending = autoStartPending;
        this.contract = contract;
        downloadExecutor = new DefaultPool(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DownloadThreadFactory(Thread.NORM_PRIORITY - 2, "mfdownloader-"));
//        downloadQueue = new PriorityBlockingQueue();
        runningDownloads = new ConcurrentHashMap<>();
        downloadOnStopLocks = new ConcurrentHashMap<>();
        if (monitorCallbackPeriod == 0) monitorCallbackPeriod = ProgressMonitor.DEFAULT_PERIOD;
        progressMonitor = new ProgressMonitor(runningDownloads, monitorCallbackPeriod, dispatcher);
        progressMonitor.start();
    }

    long startDownload(Download download) {
        MFLog.d("runningDownloads.size()=" + runningDownloads.size());
//        MFLog.d("runningDownloads size time:" + (System.currentTimeMillis() - time));
        long id = -1L;
        if (runningDownloads.size() < maxDownloadCount.get()) {
            download.setStatus(Download.STATUS_RUNNING);
            id = contract.insertDownload(download);
            if (id == -1) throw new RuntimeException("insert record fail");
            DownloadConsumer downloadConsumer = new DownloadConsumer(download, false);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(id, download);
            contract.triggerAddEvent(download);
        } else {
            download.setStatus(Download.STATUS_PENDING);
            id = contract.insertDownload(download);
            if (id == -1) throw new RuntimeException("insert record fail");
            contract.triggerAddEvent(download);
            MFLog.d("insert pending id:" + id);
        }
        return id;
    }

    void startPendingDownload(Download download) {
        if (runningDownloads.size() < maxDownloadCount.get()) {
            DownloadConsumer downloadConsumer = new DownloadConsumer(download, false);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(download.getId(), download);
//            progressMonitor.addMonitorContent(download.getId());
            //store wrapper
            download.setStatus(Download.STATUS_RUNNING);
            contract.updateDownload(download);
        } else
            MFLog.d("download num max");
    }

    void setDownloadPaused(long id) {
        if (!runningDownloads.containsKey(id))
            throw new IllegalArgumentException("can not pause a not running download");
        final Download download = runningDownloads.get(id);
        download.setStatus(Download.STATUS_PAUSED);
    }

    void resumeDownload(Download download) {
        if (runningDownloads.size() < maxDownloadCount.get()) {
            DownloadConsumer downloadConsumer = new DownloadConsumer(download, true);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(download.getId(), download);
//            progressMonitor.addMonitorContent(download.getId());
            download.setStatus(Download.STATUS_RUNNING);
        } else {
            download.setStatus(Download.STATUS_PENDING);
        }
        long id = contract.updateDownload(download);
        if (id == -1) throw new RuntimeException("update download fail");
    }

    void cancelDownload(Download download) {
        int status = download.getStatus();
        switch (status) {
            case Download.STATUS_PENDING:
            case Download.STATUS_PAUSED:
            case Download.STATUS_FAILED:
                long id = contract.deleteDownload(download);
                if (id == -1) throw new RuntimeException("delete download fail");
                break;
            case Download.STATUS_RUNNING:
                if (!runningDownloads.containsKey(download.getId()))
                    throw new RuntimeException("cancel download fail");
                runningDownloads.get(download.getId()).setStatus(Download.STATUS_CANCELLED);
                break;
        }
    }

    /**
     * wait for all block downloaded
     *
     * @param id
     */
    private void waitForDownloadStopLock(long id) {
        CountDownLatch latch = downloadOnStopLocks.get(id);
        if (latch == null) {
            return;
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void countDownLock(long id) {
        CountDownLatch latch = null;
        try {
            latch = downloadOnStopLocks.get(id);
        } finally {
            if (latch != null) latch.countDown();
        }
    }

    private BlockDownloadListener blockDownloadListener = new BlockDownloadListener() {

        @Override
        public boolean onBytesDownload(long downloadId, int blockIndex, long current, long total, long bytesThisStep) {
            final Download download = runningDownloads.get(downloadId);
            synchronized (download) {
                download.setCurrentBytes(download.getCurrentBytes() + bytesThisStep);
                boolean shouldProcess = download.getStatus() == Download.STATUS_PAUSED ||
                        download.getStatus() == Download.STATUS_CANCELLED;
//            MFLog.d("downloadId:" + downloadId + " current:" + currentBytes + " total:" + wrapper.totalBytes);
//                if (!shouldProcess)
//                    contract.triggerProgressEvent(download);
                return !shouldProcess;
            }
        }

        @Override
        public void onDownloadStop(long downloadId, int blockIndex, long currentBytes) {
            final Download download = runningDownloads.get(downloadId);
            synchronized (download) {//download access by each block download thread
                Block block = download.getBlocks().get(blockIndex);
                block.setDownloadedBytes(currentBytes);
            }
            countDownLock(downloadId);
        }

        @Override
        public void onDownloadFail(long downloadId, int blockIndex, long currentBytes) {
            final Download download = runningDownloads.get(downloadId);
            synchronized (download) {//download access by each block download thread
                Block block = download.getBlocks().get(blockIndex);
                block.setDownloadedBytes(currentBytes);
                download.setStatus(Download.STATUS_FAILED);
            }
            countDownLock(downloadId);
        }
    };

    private ProgressMonitor.MonitorDispatcher dispatcher = new ProgressMonitor.MonitorDispatcher() {
        @Override
        public void onUpdate() {
            Iterator<Map.Entry<Long, Download>> iterator =
                    runningDownloads.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Download> entry = iterator.next();
//                MFLog.d(entry.getKey() + "#" + entry.getValue().showSpeed());
                final Download download = runningDownloads.get(entry.getKey());
                synchronized (download) {
                    boolean shouldProcess = download.getStatus() == Download.STATUS_PAUSED ||
                            download.getStatus() == Download.STATUS_CANCELLED;
                    if (!shouldProcess)
                        contract.triggerProgressEvent(download);
                }
            }
        }
    };

    private class DownloadConsumer implements Runnable {
        private final Download download;
        private final boolean resume;

        private DownloadConsumer(Download download, boolean resume) {
            this.download = download;
            this.resume = resume;
        }

        @Override
        public void run() {
            //call back on start
            contract.triggerStartEvent(download);
            try {
                if (!resume) setupDownload(download);
            } catch (IOException e) {
                e.printStackTrace();
                download.setStatus(Download.STATUS_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
                download.setStatus(Download.STATUS_FAILED);
            } finally {
                contract.updateDownload(download);
            }
            if (download.getStatus() == Download.STATUS_FAILED) {
                contract.triggerFailEvent(download);
                return;
            }
            if (download.getStatus() == Download.STATUS_PAUSED) {
                contract.triggerPauseEvent(download);
                return;
            }
            List<Block> blocks = download.getBlocks();
            int runningConsumerCount = 0;
            for (int i = 0, size = blocks.size(); i < size; ++i) {
                final Block block = blocks.get(i);
                if (block.isComplete()) {
//                    block.setStop(true);
                    continue;
                }
                long startPos = block.getStartPos() + block.getDownloadedBytes();
                BlockConsumer consumer = new BlockConsumer(block.getIndex(), startPos, block.getEndPos(),
                        download.getUri(), download.getTargetDir() + File.separator + download.getFileName(),
                        download.getId());
//                MFLog.d("init block pos#s:" + startPos + "#e:" + block.getEndPos());
                downloadExecutor.execute(consumer);
                ++runningConsumerCount;
            }
            downloadOnStopLocks.put(download.getId(), new CountDownLatch(runningConsumerCount));
            waitForDownloadStopLock(download.getId());

            contract.triggerProgressEvent(download);//update the progress to 100%
            runningDownloads.remove(download.getId());
//            progressMonitor.removeMonitorContent(download.getId());
            downloadOnStopLocks.remove(download.getId());

            if (download.getCurrentBytes() == download.getTotalBytes()) {
                download.setStatus(Download.STATUS_SUCCESSFUL);
                MFLog.d("wrapper.setStatus(Download.STATUS_SUCCESSFUL);id:" + download.getId());
                contract.triggerCompleteEvent(download);
                contract.updateDownload(download);
            } else if (download.getStatus() == Download.STATUS_PAUSED) {
                //stream I/O loop interrupt by paused
                contract.triggerPauseEvent(download);
                contract.updateDownload(download);
            } else if (download.getStatus() == Download.STATUS_CANCELLED) {
                //cancel
                contract.deleteDownload(download);
                contract.triggerCancelEvent(download);
            } else {//stopped by error
                download.setStatus(Download.STATUS_FAILED);
                contract.triggerFailEvent(download);
                contract.updateDownload(download);
            }

            MFLog.d("autoStartPending=" + autoStartPending);
            if (autoStartPending) {
                Download pendingDownload = contract.queryFirstPendingDownload();
                if (pendingDownload != null) {
                    MFLog.d("pendingDownload id=" + pendingDownload.getId());
                    startPendingDownload(pendingDownload);
                } else MFLog.d("pendingDownload=null");
            }
        }

        /**
         * init new download, fill in blocks and total bytes
         *
         * @param download
         * @throws IOException
         */
        void setupDownload(Download download) throws IOException {
            final Pair<Long, String> lenNamePair = downloadOperator.getRemoteFileLengthAndName(download.getUri());
            if (lenNamePair != null) {
                List<Block> blocks = downloadOperator.split2Block(lenNamePair.first);
                MFLog.d("file size:" + lenNamePair.first);
                download.setBlocks(blocks);
                /**
                 * wrapper blocks access by every thread of download block
                 */
                download.setTotalBytes(lenNamePair.first);
                String fileName = download.getFileName();
                if (fileName == null) {//user did not set file name, use generated file name
                    fileName = lenNamePair.second;
                    download.setFileName(fileName);
                }
                if (!downloadOperator.createFile(lenNamePair.first,
                        download.getTargetDir() + File.separator + fileName))
                    throw new IOException("Create file fail");
            } else {
                throw new IOException("setup download fail");
            }
        }
    }

    private class BlockConsumer implements Runnable {
        private final int blockIndex;
        private final long startPos, endPos;
        private final String uri;
        private final String targetFilePath;
        private final long downloadId;

        private BlockConsumer(int blockIndex, long startPos, long endPos, String uri,
                              String targetFilePath, long downloadId) {
            this.blockIndex = blockIndex;
            this.startPos = startPos;
            this.endPos = endPos;
            this.uri = uri;
            this.targetFilePath = targetFilePath;
            this.downloadId = downloadId;
        }

        @Override
        public void run() {
            downloadOperator.downloadBlock(downloadId, startPos, endPos, blockIndex, uri, new File(targetFilePath),
                    blockDownloadListener, DefaultDownloadOperator.BUFFER_SIZE);
        }
    }

    private class DefaultPool extends ThreadPoolExecutor {
        public DefaultPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public DefaultPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public DefaultPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public DefaultPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            MFLog.d("beforeExecute thread:" + t.getName());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            MFLog.d("afterExecute thread:" + Thread.currentThread().getName());
        }
    }
}
