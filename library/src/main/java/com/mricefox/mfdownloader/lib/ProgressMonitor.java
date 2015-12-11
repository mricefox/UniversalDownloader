package com.mricefox.mfdownloader.lib;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private final ConcurrentHashMap<Long, Download> runningDownloads;
    private long callbackPeriod;
    private boolean start;
    private long internalUpdatePeriod;//time interval of internal update the speed data
    /**
     * interface between {@link DownloadConsumerExecutor}
     */
    private MonitorDispatcher dispatcher;

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
    }

    public void start() {
        if (!start) {
            progressMonitorExecutor.scheduleAtFixedRate(monitorTask, internalUpdatePeriod, internalUpdatePeriod, TimeUnit.MILLISECONDS);
            progressMonitorExecutor.scheduleAtFixedRate(dispatchTask, callbackPeriod, callbackPeriod, TimeUnit.MILLISECONDS);
            start = true;
        }
    }

    public void stop() {
        progressMonitorExecutor.shutdownNow();
    }

//    public void addMonitorContent(long id) {
//        monitorContents.put(id, new MonitorContent());
//    }
//
//    public void removeMonitorContent(long id) {
//        monitorContents.remove(id, new MonitorContent());
//    }

    /**
     * update per second
     *
     * @param id
     */
    private void update(long id) {
        Download download = runningDownloads.get(id);
//        MonitorContent content = monitorContents.get(id);
        long currentBytes;
        synchronized (download) {
            currentBytes = download.getCurrentBytes();
            download.setDownloadTimeMills(download.getDownloadTimeMills() + internalUpdatePeriod);
            download.setBytesPerSecondNow((currentBytes - download.getPrevBytes()) * 1000 / internalUpdatePeriod);
            download.setPrevBytes(currentBytes);
            download.setBytesPerSecondMax(Math.max(download.getBytesPerSecondMax(), download.getBytesPerSecondNow()));
            download.setBytesPerSecondAverage(
                    Math.round((currentBytes + .0f) * 1000 / download.getDownloadTimeMills()));
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
            Iterator<Map.Entry<Long, Download>> iterator = runningDownloads.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Download> entry = iterator.next();
                update(entry.getKey());
            }
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
