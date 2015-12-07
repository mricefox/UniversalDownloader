package com.mricefox.mfdownloader.lib;

import com.mricefox.mfdownloader.lib.assist.L;
import com.mricefox.mfdownloader.lib.operator.BlockDownloadListener;
import com.mricefox.mfdownloader.lib.operator.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.operator.DownloadOperator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private ExecutorService downloadExecutor;
    private BlockingQueue downloadQueue;
    private DownloadOperator downloadOperator;
    private final ConcurrentHashMap<Long, DownloadWrapper> runningDownloads;
    private Contract contract;
    private ConcurrentHashMap<Long, CountDownLatch> downloadOnStopLocks;
    private AtomicInteger maxDownloadCount;
    private boolean autoStartPending;

    DownloadConsumerExecutor(DownloadOperator downloadOperator, Contract contract, int maxDownloadCount, boolean autoStartPending) {
        this.maxDownloadCount = new AtomicInteger(maxDownloadCount);
        this.downloadOperator = downloadOperator;
        this.autoStartPending = autoStartPending;
        this.contract = contract;
        downloadExecutor = new DefaultPool(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "download-t-"));
        downloadQueue = new PriorityBlockingQueue();
        runningDownloads = new ConcurrentHashMap<>();
        downloadOnStopLocks = new ConcurrentHashMap<>();
    }

    private BlockDownloadListener blockDownloadListener = new BlockDownloadListener() {

        @Override
        public boolean onBytesDownload(long downloadId, int blockIndex, long current, long total, long bytesThisStep) {
            final DownloadWrapper wrapper = runningDownloads.get(downloadId);
            wrapper.setCurrentBytes(wrapper.getCurrentBytes() + bytesThisStep);
//            L.d("downloadId:" + downloadId + " current:" + currentBytes + " total:" + wrapper.totalBytes);
//            contract.updateDownload(wrapper);
            contract.fireProgressEvent(wrapper);
            return wrapper.getStatus() != Download.STATUS_PAUSED;
        }

        @Override
        public void onDownloadStop(long downloadId, int blockIndex, long currentBytes) {
            final DownloadWrapper wrapper = runningDownloads.get(downloadId);
            synchronized (wrapper) {//wrapper access by each block download thread
                Block block = wrapper.getBlocks().get(blockIndex);
                block.setDownloadedBytes(currentBytes);
            }
            countDownLock(downloadId);

//            Block block = wrapper.getBlocks().get(blockIndex);
//            block.setDownloadedBytes(currentBytes);
//            if (wrapper.getCurrentBytes() == wrapper.getTotalBytes()) {
//                runningDownloads.remove(downloadId);
//                wrapper.setStatus(Download.STATUS_SUCCESSFUL);
//                contract.updateDownload(wrapper);
//                contract.fireCompleteEvent(wrapper);
//            } else if (wrapper.getStatus() == Download.STATUS_PAUSED) {
//                //stream I/O loop interrupt by paused
//                block.setStop(true);
//                if (wrapper.allBlockStopped()) {
//                    L.d("allBlockStopped");
//                    runningDownloads.remove(downloadId);
//                    contract.updateDownload(wrapper);
//                    contract.firePauseEvent(wrapper);
//                } else {
//                    L.d("not allBlockStopped");
//                }
//            }
        }

        @Override
        public void onDownloadFail(long downloadId, int blockIndex, long currentBytes) {
            final DownloadWrapper wrapper = runningDownloads.get(downloadId);
            synchronized (wrapper) {//wrapper access by each block download thread
                Block block = wrapper.getBlocks().get(blockIndex);
                block.setDownloadedBytes(currentBytes);
            }
            countDownLock(downloadId);
        }
    };

    long startDownload(DownloadWrapper wrapper) {
        long time = System.currentTimeMillis();
        L.d("runningDownloads.size()=" + runningDownloads.size());
        L.d("runningDownloads size time:" + (System.currentTimeMillis() - time));
        long id = -1L;
        if (runningDownloads.size() < maxDownloadCount.get()) {
            id = contract.insertDownload(wrapper);
            if (id == -1) throw new RuntimeException("insert record fail");
            DownloadConsumer downloadConsumer = new DownloadConsumer(wrapper, false);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(id, wrapper);
            //store wrapper
            wrapper.setStatus(Download.STATUS_RUNNING);
            contract.updateDownload(wrapper);
            contract.fireAddEvent(wrapper);
        } else {
            wrapper.setStatus(Download.STATUS_PENDING);
            id = contract.insertDownload(wrapper);
            if (id == -1) throw new RuntimeException("insert record fail");
            contract.fireAddEvent(wrapper);
            L.d("insert pending id:" + id);
        }
        return id;
    }

    void startPendingDownload(DownloadWrapper wrapper) {
        if (runningDownloads.size() < maxDownloadCount.get()) {
            DownloadConsumer downloadConsumer = new DownloadConsumer(wrapper, false);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(wrapper.getDownload().getId(), wrapper);
            //store wrapper
            wrapper.setStatus(Download.STATUS_RUNNING);
            contract.updateDownload(wrapper);
        } else
            L.d("download num max");
    }

    void setDownloadPaused(long id) {
        if (!runningDownloads.containsKey(id))
            throw new IllegalArgumentException("can not pause a not running download");
        final DownloadWrapper wrapper = runningDownloads.get(id);
        wrapper.setStatus(Download.STATUS_PAUSED);
    }

    void resumeDownload(DownloadWrapper wrapper) {
        if (runningDownloads.size() < maxDownloadCount.get()) {
            DownloadConsumer downloadConsumer = new DownloadConsumer(wrapper, true);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(wrapper.getDownload().getId(), wrapper);
            wrapper.setStatus(Download.STATUS_RUNNING);
        } else {
            wrapper.setStatus(Download.STATUS_PENDING);
        }
        long id = contract.updateDownload(wrapper);
        if (id == -1) throw new RuntimeException("update download fail");
    }

    void cancelDownload(DownloadWrapper wrapper) {
// TODO: 2015/12/7
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

    private class DownloadConsumer implements Runnable {
        private DownloadWrapper wrapper;
        private boolean resume;

        DownloadConsumer(DownloadWrapper wrapper, boolean resume) {
            this.wrapper = wrapper;
            this.resume = resume;
        }

        @Override
        public void run() {
            //call back on start
            contract.fireStartEvent(wrapper);
            try {
                if (!resume) setupDownload(wrapper);
            } catch (IOException e) {
                e.printStackTrace();
                wrapper.setStatus(Download.STATUS_FAILED);
            } catch (Exception e) {
                e.printStackTrace();
                wrapper.setStatus(Download.STATUS_FAILED);
            } finally {
                contract.updateDownload(wrapper);
            }
            if (wrapper.getStatus() == Download.STATUS_FAILED) {
                contract.fireFailEvent(wrapper);
                return;
            }
            if (wrapper.getStatus() == Download.STATUS_PAUSED) {
                contract.firePauseEvent(wrapper);
                return;
            }
            List<Block> blocks = wrapper.getBlocks();
            int runningConsumerCount = 0;
            for (int i = 0, size = blocks.size(); i < size; ++i) {
                final Block block = blocks.get(i);
                if (block.isComplete()) {
//                    block.setStop(true);
                    continue;
                }
                long startPos = block.getStartPos() + block.getDownloadedBytes();
                BlockConsumer consumer = new BlockConsumer(block.getIndex(), startPos, block.getEndPos(),
                        wrapper.getDownload().getUri(), wrapper.getDownload().getTargetFilePath(),
                        wrapper.getDownload().getId());
//                L.d("init block pos#s:" + startPos + "#e:" + block.getEndPos());
                downloadExecutor.execute(consumer);
                ++runningConsumerCount;
            }
            downloadOnStopLocks.put(wrapper.getDownload().getId(), new CountDownLatch(runningConsumerCount));
            waitForDownloadStopLock(wrapper.getDownload().getId());

            runningDownloads.remove(wrapper.getDownload().getId());
            downloadOnStopLocks.remove(wrapper.getDownload().getId());

            if (wrapper.getCurrentBytes() == wrapper.getTotalBytes()) {
                wrapper.setStatus(Download.STATUS_SUCCESSFUL);
                L.d("wrapper.setStatus(Download.STATUS_SUCCESSFUL);id:" + wrapper.getDownload().getId());
                contract.fireCompleteEvent(wrapper);
            } else if (wrapper.getStatus() == Download.STATUS_PAUSED) {
                //stream I/O loop interrupt by paused
                contract.firePauseEvent(wrapper);
            } else {//stopped by error
                wrapper.setStatus(Download.STATUS_FAILED);
                contract.fireFailEvent(wrapper);
            }
            contract.updateDownload(wrapper);

            L.d("autoStartPending=" + autoStartPending);
            if (autoStartPending) {
                DownloadWrapper pendingDownload = contract.queryFirstPendingDownload();
                if (pendingDownload != null) {
                    L.d("pendingDownload id=" + pendingDownload.getDownload().getId());
                    startPendingDownload(pendingDownload);
                } else L.d("pendingDownload=null");
            }
        }

        /**
         * init new download, fill in blocks and total bytes
         *
         * @param wrapper
         * @throws IOException
         */
        void setupDownload(DownloadWrapper wrapper) throws IOException {
            final long fileLength = downloadOperator.getRemoteFileLength(wrapper.getDownload().getUri());
            if (fileLength != -1) {
                List<Block> blocks = downloadOperator.split2Block(fileLength);
                L.d("file size:" + fileLength);
                if (downloadOperator.createFile(fileLength, wrapper.getDownload().getTargetFilePath())) {
                    wrapper.setBlocks(blocks);
                    /**
                     * wrapper blocks access by every thread of download block
                     */
                    wrapper.setTotalBytes(fileLength);
                } else
                    throw new IOException("Create file fail");
            } else {
                throw new IOException("Remote file length = -1");
            }
        }
    }

    private class BlockConsumer implements Runnable {
        private int blockIndex;
        private long startPos, endPos;
        private String uri;
        private String targetFilePath;
        private long downloadId;

        BlockConsumer(int blockIndex, long startPos, long endPos, String uri,
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

    private class DefaultThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
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
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
        }
    }
}
