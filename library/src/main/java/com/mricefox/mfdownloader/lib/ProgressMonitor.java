package com.mricefox.mfdownloader.lib;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/10
 */
public class ProgressMonitor {
    /**
     * {@value}
     */
    public final static long MIN_PERIOD = 1000;
    /**
     * {@value}
     */
    public final static long MAX_PERIOD = 5000;
    /**
     * {@value}
     */
    public final static long DEFAULT_PERIOD = 1000;

    private final ScheduledExecutorService progressMonitorExecutor;
    //    private final ConcurrentHashMap<Long, MonitorContent> monitorContents;
    private final ExecutorService manualUpdateExecutor;
    private final ConcurrentHashMap<Long, Download> runningDownloads;
    private long callbackPeriod;
    private AtomicBoolean start = new AtomicBoolean(false);
    private long internalUpdatePeriod;//time interval of internal update the speed data
    /**
     * interface between {@link DownloadConsumerExecutor}
     */
    private MonitorDispatcher dispatcher;
//    private Object updateLock = new Object();

    /**
     * @param runningDownloads
     * @param callbackPeriod   MILLISECONDS must >  {@link ProgressMonitor#MIN_PERIOD} and < {@link ProgressMonitor#MAX_PERIOD}
     */
    public ProgressMonitor(ConcurrentHashMap<Long, Download> runningDownloads, long callbackPeriod, MonitorDispatcher dispatcher) {
        this.runningDownloads = runningDownloads;
//        monitorContents = new ConcurrentHashMap<>();
        progressMonitorExecutor = new ScheduledThreadPoolExecutor(2,
                new DownloadThreadFactory(Thread.NORM_PRIORITY - 2, "mf-monitor-"));
        this.internalUpdatePeriod = DEFAULT_PERIOD;
        this.dispatcher = dispatcher;
        if (callbackPeriod < MIN_PERIOD || callbackPeriod > MAX_PERIOD) {
            throw new IllegalArgumentException("period out of range");
        }
        this.callbackPeriod = callbackPeriod;
        manualUpdateExecutor = Executors.newSingleThreadExecutor(
                new DownloadThreadFactory(Thread.NORM_PRIORITY - 2, "mf-manual-"));
    }

    public void start() {
        if (!start.get()) {
            /**
             * monitorTask and dispatchTask execute in same ScheduledThreadPoolExecutor, so they are executed in parallel
             */
            progressMonitorExecutor.scheduleAtFixedRate(monitorTask, internalUpdatePeriod, internalUpdatePeriod, TimeUnit.MILLISECONDS);
            progressMonitorExecutor.scheduleAtFixedRate(dispatchTask, callbackPeriod, callbackPeriod, TimeUnit.MILLISECONDS);
            start.set(true);
        }
    }

    public void stop() {
        progressMonitorExecutor.shutdownNow();
        start.set(false);
    }

    /**
     * manual update download and invoke callback
     */
    public void manualUpdate() {
        if (!start.get()) return;
        /**
         * execute in parallel
         */
        manualUpdateExecutor.execute(monitorTask);
        manualUpdateExecutor.execute(dispatchTask);
    }
//    public void addMonitorContent(long id) {
//        monitorContents.put(id, new MonitorContent());
//    }
//
//    public void removeMonitorContent(long id) {
//        monitorContents.remove(id, new MonitorContent());
//    }

    /**
     * update per second, but running download will be removed on complete,so download progress maybe not 100% on
     * the right time.
     *
     * @param id
     */
    private void update(long id) {
        Download download = runningDownloads.get(id);
//        MonitorContent content = monitorContents.get(id);
        long currentBytes;
        synchronized (download) {
            currentBytes = download.getCurrentBytes();
            download.setElapseTimeMills(download.getElapseTimeMills() + internalUpdatePeriod);
            download.setBytesPerSecondNow((currentBytes - download.getPrevBytes()) * 1000 / internalUpdatePeriod);
            download.setPrevBytes(currentBytes);
            download.setBytesPerSecondMax(Math.max(download.getBytesPerSecondMax(), download.getBytesPerSecondNow()));
            download.setBytesPerSecondAverage(
                    Math.round((currentBytes + .0f) * 1000 / download.getElapseTimeMills()));
            if (download.getBytesPerSecondNow() <= 0) download.setTimeRemain(-1);
            else
                download.setTimeRemain((download.getTotalBytes() - currentBytes) / download.getBytesPerSecondNow());
        }
//        synchronized (content) {
//            content.monitor_mills += internalUpdatePeriod;
//            content.bytes_per_second_now = (currentBytes - content.current_bytes) * 1000 / internalUpdatePeriod;
//            content.current_bytes = currentBytes;
//            content.bytes_per_second_max = Math.max(content.bytes_per_second_max, content.bytes_per_second_now);
//            content.bytes_per_second_average =
//                    Math.round((content.current_bytes + .0f) * 1000 / content.monitor_mills);
//        }
    }

    private Runnable monitorTask = new Runnable() {
        @Override
        public void run() {
//            synchronized (updateLock) {
//                try {
//                    updateLock.wait();
            Iterator<Map.Entry<Long, Download>> iterator = runningDownloads.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Download> entry = iterator.next();
                update(entry.getKey());
            }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    updateLock.notifyAll();
//                }
//            }
        }
    };

//    private Runnable dispatchTask = new Runnable() {
//        @Override
//        public void run() {
//            if (dispatcher != null) dispatcher.onUpdate(monitorContents);
//        }
//    };

    private Runnable dispatchTask = new Runnable() {
        @Override
        public void run() {
            if (dispatcher != null) dispatcher.onUpdate();
        }
    };

//    public static class MonitorContent {
//        long bytes_per_second_now;
//        long bytes_per_second_max;
//        long bytes_per_second_average;
//        long current_bytes;
//        long monitor_mills;
//
//        @Override
//        public String toString() {
//            return "bytes:" + StringUtil.displayFilesize(current_bytes) + "#mills:" + monitor_mills / 1000 +
//                    "#bpsn:" + StringUtil.displayFilesize(bytes_per_second_now) + "bpsm:" +
//                    StringUtil.displayFilesize(bytes_per_second_max) +
//                    "bpsa:" + StringUtil.displayFilesize(bytes_per_second_average);
//        }
//    }

//    public interface MonitorDispatcher {
//        void onUpdate(ConcurrentHashMap<Long, MonitorContent> monitorContents);
//    }

    public interface MonitorDispatcher {
        void onUpdate();
    }
}
