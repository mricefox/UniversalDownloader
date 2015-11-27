package com.mricefox.mfdownloader.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    static int consumerCount = 0;
    private int maxDownloadCount;

    //    private final Executor initializeExecutor;
    private final Executor downloadExecutor;
    private ThreadGroup initDownloadThreadGroup;
    //    private BlockingQueue downloadQueue;
    private DownloadOperator downloadOperator;

    DownloadConsumerExecutor(int maxDownloadCount, DownloadOperator downloadOperator) {
        this.maxDownloadCount = maxDownloadCount;
        this.downloadOperator = downloadOperator;
        downloadExecutor =
                Executors.newCachedThreadPool(new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "download-t-"));
//        initializeExecutor =
//                Executors.newFixedThreadPool(maxDownloadCount, new DefaultThreadFactory(Thread.NORM_PRIORITY - 2, "init-d-t-"));
//        downloadQueue = new PriorityBlockingQueue();
    }

    void submitDownload(Download download) {
        InitDownloadTask initDownloadTask = new InitDownloadTask();
        initDownloadTask.download = download;
        downloadExecutor.execute(initDownloadTask);
    }

    void downloadBlocks(List<Block> blocks) {

    }

    private class InitDownloadTask implements Runnable {
        Download download;

        @Override
        public void run() {
//            final long fileLength = downloadOperator.getRemoteFileLength(download.getUrlStr());
            try {
                InputStream is = downloadOperator.openStream(download.getUri());
                File targetFile = new File(download.getTargetFilePath());

                if (is != null) {
                    List<Block> blocks = downloadOperator.split2Block(is.available());
                    downloadOperator.createFile(is.available(), download.getTargetFilePath());
                    for (int i = 0, size = blocks.size(); i < size; ++i) {
                        DownloadConsumer consumer =
                                new DownloadConsumer(targetFile, blocks.get(i), is);
                        downloadExecutor.execute(consumer);
                    }
                } else {
                    L.e("InputStream == null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DownloadConsumer implements Runnable {
        //        private Download download;
        private File targetFile;
        private Block block;
        private InputStream is;

        DownloadConsumer(File targetFile, Block block, InputStream is) {
//            this.download = download;
            this.is = is;
            this.block = block;
            this.targetFile = targetFile;
        }

        @Override
        public void run() {
            try {
                downloadOperator.downloadBlock(block, is, targetFile, null, DefaultDownloadOperator.BUFFER_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
