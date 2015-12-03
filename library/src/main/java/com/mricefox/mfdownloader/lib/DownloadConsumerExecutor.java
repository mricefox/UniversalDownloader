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
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private final ExecutorService downloadExecutor;
    private BlockingQueue downloadQueue;
    private DownloadOperator downloadOperator;
    private ConcurrentHashMap<Long, DownloadWrapper> runningDownloads;
    private Contract contract;
    private ConcurrentHashMap<Long, CountDownLatch> downloadOnStopLocks;
    private int maxDownloadCount;
    private boolean autoStartPending;

    DownloadConsumerExecutor(DownloadOperator downloadOperator, Contract contract, int maxDownloadCount, boolean autoStartPending) {
        this.maxDownloadCount = maxDownloadCount;
        this.downloadOperator = downloadOperator;
        this.autoStartPending = autoStartPending;
        this.contract = contract;
        downloadExecutor =
                Executors.newCachedThreadPool(new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "download-t-"));
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
            CountDownLatch latch = downloadOnStopLocks.get(downloadId);
            if (latch != null) latch.countDown();

//            Block block = wrapper.getBlocks().get(blockIndex);
//            block.setDownloadedBytes(currentBytes);
//            if (wrapper.getCurrentBytes() == wrapper.getTotalBytes()) {
//                runningDownloads.remove(downloadId);
//                wrapper.setStatus(Download.STATUS_SUCCESSFUL);
//                contract.updateDownload(wrapper);
//                fireCompleteEvent(wrapper);
//            } else if (wrapper.getStatus() == Download.STATUS_PAUSED) {
//                //stream I/O loop interrupt by paused
//                block.setStop(true);
//                if (wrapper.allBlockStopped()) {
//                    L.d("allBlockStopped");
//                    runningDownloads.remove(downloadId);
//                    contract.updateDownload(wrapper);
//                    firePauseEvent(wrapper);
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
            CountDownLatch latch = downloadOnStopLocks.get(downloadId);
            if (latch != null) latch.countDown();
        }
    };

    long addDownload(DownloadWrapper wrapper) {
//        if (!checkCanAdd()) return -1;
        long id = -1;
        if (runningDownloads.size() < maxDownloadCount) {
            id = contract.insertDownload(wrapper);
            if (id == -1) return id;
            DownloadConsumer downloadConsumer = new DownloadConsumer(wrapper, false);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(id, wrapper);
            wrapper.setStatus(Download.STATUS_RUNNING);
            contract.updateDownload(wrapper);
        } else {
            wrapper.setStatus(Download.STATUS_PENDING);
            id = contract.insertDownload(wrapper);
        }
        return id;
    }

//    private boolean checkCanAdd() {
//        // TODO: 2015/11/30
//        return true;
//    }

    void setDownloadPaused(long id) {
        if (!runningDownloads.containsKey(id))
            throw new IllegalArgumentException("can not pause a not running download");
        final DownloadWrapper wrapper = runningDownloads.get(id);
        wrapper.setStatus(Download.STATUS_PAUSED);
    }

    void resumeDownload(DownloadWrapper wrapper) {
        if (runningDownloads.size() < maxDownloadCount) {
            DownloadConsumer downloadConsumer = new DownloadConsumer(wrapper, true);
            downloadExecutor.execute(downloadConsumer);
            runningDownloads.put(wrapper.getId(), wrapper);
            wrapper.setStatus(Download.STATUS_RUNNING);
        } else {
            wrapper.setStatus(Download.STATUS_PENDING);
        }
        long id = contract.updateDownload(wrapper);
        if (id == -1) throw new RuntimeException("update download fail");
    }

//    private long generateDownloadId() {// TODO: 2015/12/1
//        return downloadId++;
//    }

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

//    private void fireStartEvent(DownloadWrapper wrapper) {
//        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
//        if (listener != null) listener.onStart(wrapper.getId());
//    }
//
//    private void fireFailEvent(DownloadWrapper wrapper) {
//        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
//        if (listener != null) listener.onFailed(wrapper.getId());
//    }
//
//    private void fireProgressEvent(DownloadWrapper wrapper) {
//        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
//        if (listener != null)
//            listener.onProgressUpdate(wrapper.getId(), wrapper.getCurrentBytes(), wrapper.getTotalBytes(), 0);
//    }
//
//    private void fireCompleteEvent(final DownloadWrapper wrapper) {
//        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
//        if (listener != null) listener.onComplete(wrapper.getId());
//    }
//
//    private void firePauseEvent(final DownloadWrapper wrapper) {
//        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
//        if (listener != null) listener.onPaused(wrapper.getId());
//    }

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
                        wrapper.getDownload().getUri(), wrapper.getDownload().getTargetFilePath(), wrapper.getId());
                L.d("init block pos s:" + startPos + "#e:" + block.getEndPos());
                downloadExecutor.execute(consumer);
                ++runningConsumerCount;
            }
            downloadOnStopLocks.put(wrapper.getId(), new CountDownLatch(runningConsumerCount));
            waitForDownloadStopLock(wrapper.getId());
            runningDownloads.remove(wrapper.getId());
            downloadOnStopLocks.remove(wrapper.getId());
            if (wrapper.getCurrentBytes() == wrapper.getTotalBytes()) {
                wrapper.setStatus(Download.STATUS_SUCCESSFUL);
                contract.updateDownload(wrapper);
                contract.fireCompleteEvent(wrapper);
            } else if (wrapper.getStatus() == Download.STATUS_PAUSED) {
                //stream I/O loop interrupt by paused
                contract.updateDownload(wrapper);
                contract.firePauseEvent(wrapper);
            } else {//stopped by error
                wrapper.setStatus(Download.STATUS_FAILED);
                contract.updateDownload(wrapper);
                contract.fireFailEvent(wrapper);
            }
        }

        void setupDownload(DownloadWrapper wrapper) throws IOException {
            if (resume) return;
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
}
