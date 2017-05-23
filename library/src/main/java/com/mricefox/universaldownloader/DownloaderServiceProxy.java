package com.mricefox.universaldownloader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public class DownloaderServiceProxy implements ServiceConnection {
    private static final String TAG = "ud.proxy";
    private static final long BIND_SERVICE_TIMEOUT = 5_000;

    private IDownloaderService service;
    private Context appContext;
    private LinkedBlockingQueue<Job> jobQueue = new LinkedBlockingQueue();
    private ReentrantLock accessLock = new ReentrantLock();
    private long bindServiceAt = -1L;
    private AtomicBoolean isRunning = new AtomicBoolean();
    private Persistence persistence;

    private final static class InstanceHolder {
        private final static DownloaderServiceProxy INSTANCE = new DownloaderServiceProxy();
    }

    private DownloaderServiceProxy() {
    }

    public static DownloaderServiceProxy inst() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context context) {
        this.appContext = context.getApplicationContext();
    }


    public int enqueue(DownloadTask task) {
        ensureRunning();

        jobQueue.offer(new Job() {
            @Override
            protected void execute() {

            }
        });

        return -1;
    }

    private boolean startUDService() {
        Intent intent = new Intent(appContext, DownloaderService.class);
        appContext.startService(intent);
        return appContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private void shutDownUDService() {
        if (service != null) {
            appContext.unbindService(this);
            appContext.stopService(new Intent(appContext, DownloaderService.class));
            service = null;
        }
        accessLock.lock();
        try {
            bindServiceAt = -1;
        } finally {
            accessLock.unlock();
        }
    }

    private void ensureRunning() {
        if (!isRunning.get()) {
            worker.start();
            isRunning.set(true);
        }
    }

    private boolean tryBindService() throws InterruptedException {
        accessLock.lockInterruptibly();
        try {
            if (bindServiceAt == -1L) {
                bindServiceAt = System.nanoTime();
                startUDService();
            } else if (System.nanoTime() - bindServiceAt > BIND_SERVICE_TIMEOUT * 1000_000) {
                return true;
            }
            return false;
        } finally {
            accessLock.unlock();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder ib) {
        UDLogger.i(TAG, "onServiceConnected");
        service = IDownloaderService.Stub.asInterface(ib);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    private Thread worker = Utils.threadFactory("ud-proxy-worker", false).newThread(new Runnable() {
        @Override
        public void run() {
            for (; ; ) {
                if (service == null) {
                    try {
                        boolean timedOut = tryBindService();
                        if (timedOut) {
                            UDLogger.w(TAG, "tryBindService time out!");
                            // TODO: 2017/5/23 bind service fail, exit
                            break;
                        } else {
                            continue;
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        break;
                    }
                }

                try {
                    Job job = jobQueue.take();
                    job.execute();
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    break;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    });

    private abstract static class Job {
        private static final AtomicInteger ID_CURSOR = new AtomicInteger();
        private final int id;

        private Job() {
            id = ID_CURSOR.getAndIncrement();
        }

        protected abstract void execute();
    }
}
