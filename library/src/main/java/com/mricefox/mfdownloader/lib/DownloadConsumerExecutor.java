package com.mricefox.mfdownloader.lib;

import com.mricefox.mfdownloader.lib.assist.L;
import com.mricefox.mfdownloader.lib.operator.BlockDownloadListener;
import com.mricefox.mfdownloader.lib.operator.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.operator.DownloadOperator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private int maxDownloadCount;
    //    private final Executor initializeExecutor;
    private final ExecutorService downloadExecutor;
    //    private ThreadGroup initDownloadThreadGroup;
    //    private BlockingQueue downloadQueue;
    private DownloadOperator downloadOperator;
    private ConcurrentHashMap<Long, DownloadWrapper> runningDownloads;
    private Contract contract;

    DownloadConsumerExecutor(int maxDownloadCount, DownloadOperator downloadOperator, Contract contract) {
        this.maxDownloadCount = maxDownloadCount;
        this.downloadOperator = downloadOperator;
        this.contract = contract;
        downloadExecutor =
                Executors.newCachedThreadPool(new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "download-t-"));
//        initializeExecutor =
//                Executors.newFixedThreadPool(maxDownloadCount, new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "init-d-t-"));
//        downloadQueue = new PriorityBlockingQueue();
        runningDownloads = new ConcurrentHashMap<>();
    }

    private BlockDownloadListener blockDownloadListener = new BlockDownloadListener() {

        @Override
        public boolean onBytesDownload(long downloadId, int blockIndex, long current, long total, long bytesThisStep) {
            final DownloadWrapper wrapper = runningDownloads.get(downloadId);
            wrapper.setCurrentBytes(wrapper.getCurrentBytes() + bytesThisStep);
//            L.d("downloadId:" + downloadId + " current:" + currentBytes + " total:" + wrapper.totalBytes);
            contract.updateDownload(wrapper);
            fireProgressEvent(wrapper);
            return wrapper.getStatus() != Download.STATUS_PAUSED;
        }

        @Override
        public void onDownloadStop(long downloadId, int blockIndex, long currentBytes) {
            final DownloadWrapper wrapper = runningDownloads.get(downloadId);
            Block block = wrapper.getBlocks().get(blockIndex);

            block.setDownloadedBytes(currentBytes);
            if (wrapper.getCurrentBytes() == wrapper.getTotalBytes()) {
                runningDownloads.remove(downloadId);
                wrapper.setStatus(Download.STATUS_SUCCESSFUL);
                contract.updateDownload(wrapper);
                fireCompleteEvent(wrapper);
            } else if (wrapper.getStatus() == Download.STATUS_PAUSED) {
                //stream I/O loop interrupt by paused
                block.setStop(true);
                block.setStartPos(currentBytes + block.getStartPos());// FIXME: 2015/12/2
                block.setDownloadedBytes(0);
                if (wrapper.allBlockStopped()) {
                    runningDownloads.remove(downloadId);
                    contract.updateDownload(wrapper);
                    firePauseEvent(wrapper);
                }
            }
        }

        @Override
        public void onDownloadFail(long downloadId, int blockIndex) {

        }
    };

    long addDownload(DownloadWrapper wrapper) {
//        if (!checkCanAdd()) return -1;
        long id = -1;
        if (runningDownloads.size() < maxDownloadCount) {
            id = contract.insertDownload(wrapper);
            if (id == -1) return id;
            InitDownloadTask initDownloadTask = new InitDownloadTask(wrapper, false);
            downloadExecutor.execute(initDownloadTask);
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
            InitDownloadTask initDownloadTask = new InitDownloadTask(wrapper, true);
            downloadExecutor.execute(initDownloadTask);
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

    private void fireStartEvent(DownloadWrapper wrapper) {
        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
        if (listener != null) listener.onStart(wrapper.getId());
    }

    private void fireFailEvent(DownloadWrapper wrapper) {
        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
        if (listener != null) listener.onFailed(wrapper.getId());
    }

    private void fireProgressEvent(DownloadWrapper wrapper) {
        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
        if (listener != null)
            listener.onProgressUpdate(wrapper.getId(), wrapper.getCurrentBytes(), wrapper.getTotalBytes(), 0);
    }

    private void fireCompleteEvent(final DownloadWrapper wrapper) {
        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
        if (listener != null) listener.onComplete(wrapper.getId());
    }

    private void firePauseEvent(final DownloadWrapper wrapper) {
        DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
        if (listener != null) listener.onPaused(wrapper.getId());
    }

    private class InitDownloadTask implements Runnable {
        private DownloadWrapper wrapper;
        private boolean resume;

        InitDownloadTask(DownloadWrapper wrapper, boolean resume) {
            this.wrapper = wrapper;
            this.resume = resume;
        }

        @Override
        public void run() {
            //call back on start
            fireStartEvent(wrapper);
            try {
                if (!resume) setupDownload(wrapper);
                contract.updateDownload(wrapper);
            } catch (IOException e) {
                e.printStackTrace();
                fireFailEvent(wrapper);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                fireFailEvent(wrapper);
                return;
            }
            if (wrapper.getStatus() == Download.STATUS_PAUSED) {
                firePauseEvent(wrapper);
                return;
            }
            List<Block> blocks = wrapper.getBlocks();
            for (int i = 0, size = blocks.size(); i < size; ++i) {
                final Block block = blocks.get(i);
                if (block.isComplete()) continue;
                DownloadConsumer consumer = new DownloadConsumer(wrapper.getDownload().getUri(),
                        wrapper.getDownload().getTargetFilePath(), block, wrapper.getId());
                L.d("init block pos s:" + block.getStartPos() + "#e:" + block.getEndPos());
                downloadExecutor.execute(consumer);
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

    private class DownloadConsumer implements Runnable {
        private Block block;
        private String uri;
        private String targetFilePath;
        private long downloadId;

        DownloadConsumer(String uri, String targetFilePath, Block block, long downloadId) {
            this.block = block;
            this.uri = uri;
            this.targetFilePath = targetFilePath;
            this.downloadId = downloadId;
        }

        @Override
        public void run() {
            downloadOperator.downloadBlock(downloadId, block, uri, new File(targetFilePath),
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
