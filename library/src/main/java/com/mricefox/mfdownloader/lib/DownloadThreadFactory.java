package com.mricefox.mfdownloader.lib;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/10
 */
public class DownloadThreadFactory implements ThreadFactory {
    private final AtomicInteger poolNumber = new AtomicInteger(1);

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final int threadPriority;

    public DownloadThreadFactory(int threadPriority, String threadNamePrefix) {
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
