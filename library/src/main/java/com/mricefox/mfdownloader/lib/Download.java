package com.mricefox.mfdownloader.lib;

import android.os.Handler;
import android.os.Looper;

import com.mricefox.mfdownloader.lib.assist.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    public static final int STATUS_PENDING = 1 << 1;
    public static final int STATUS_RUNNING = 1 << 2;
    public static final int STATUS_PAUSED = 1 << 3;
    public static final int STATUS_SUCCESSFUL = 1 << 4;
    public static final int STATUS_FAILED = 1 << 5;
    public static final int STATUS_CANCELLED = 1 << 6;

    public static final int STATUS_CONNECTING = 1 << 7;
    public static final int STATUS_PAUSING = 1 << 8;

    //user config attrs
    private final String uri;//persistence
    private final String targetDir;//persistence
    private String fileName;
    private final int priority;//persistence
    private DownloadListener downloadListener;
    private final Handler callbackHandler;

    private long id;//persistence
    private List<Block> blocks;//persistence
    private long totalBytes;//persistence
    private long currentBytes;//persistence
    private int status;//persistence
    private long downloadTimeMills;//persistence
    private long bytesPerSecondNow;
    private long bytesPerSecondMax;
    private long bytesPerSecondAverage;
    private long prevBytes;// bytes for progress monitor
    private long timeRemain;//remain seconds in current speed

    public Download(DownloadParams params) {
        this.uri = params.getUri();
        this.targetDir = params.getTargetDir();
        this.fileName = params.getFileName();
        this.priority = params.getPriority();
        this.downloadListener = params.getDownloadListener();
        this.blocks = new ArrayList<>();

        Handler h = params.getCallbackHandler();
        //define a ui handler if constructor run in ui thread
        if (h == null && Looper.myLooper() == Looper.getMainLooper())
            this.callbackHandler = new Handler();
        else
            this.callbackHandler = h;

//        reset();
    }

    public void reset() {
        id = 0;
        blocks = new ArrayList<>();
        totalBytes = 0;
        currentBytes = 0;
        status = 0;
    }

//    public Download(String uri, String targetDir, DownloadListener listener) {
//        this.uri = uri;
//        this.targetDir = targetDir;
//        this.downloadListener = listener;
//    }

    public String getUri() {
        return uri;
    }

//    public void setUri(String uri) {
//        this.uri = uri;
//    }

    public String getTargetDir() {
        return targetDir;
    }

//    public void setTargetFilePath(String targetDir) {
//        this.targetDir = targetDir;
//    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

//    public void setPriority(int priority) {
//        this.priority = priority;
//    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Handler getCallbackHandler() {
        return callbackHandler;
    }

    public long getBytesPerSecondNow() {
        return bytesPerSecondNow;
    }

    public void setBytesPerSecondNow(long bytesPerSecondNow) {
        this.bytesPerSecondNow = bytesPerSecondNow;
    }

    public long getBytesPerSecondMax() {
        return bytesPerSecondMax;
    }

    public void setBytesPerSecondMax(long bytesPerSecondMax) {
        this.bytesPerSecondMax = bytesPerSecondMax;
    }

    public long getBytesPerSecondAverage() {
        return bytesPerSecondAverage;
    }

    public void setBytesPerSecondAverage(long bytesPerSecondAverage) {
        this.bytesPerSecondAverage = bytesPerSecondAverage;
    }

    public long getDownloadTimeMills() {
        return downloadTimeMills;
    }

    public void setDownloadTimeMills(long downloadTimeMills) {
        this.downloadTimeMills = downloadTimeMills;
    }

    public long getPrevBytes() {
        return prevBytes;
    }

    public void setPrevBytes(long prevBytes) {
        this.prevBytes = prevBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getTimeRemain() {
        return timeRemain;
    }

    public void setTimeRemain(long timeRemain) {
        this.timeRemain = timeRemain;
    }

    public String showSpeed() {
        return "bytes:" + StringUtil.displayFilesize(currentBytes) + " mills:" + downloadTimeMills / 1000 +
                " bpsn:" + StringUtil.displayFilesize(bytesPerSecondNow) + " bpsm:" +
                StringUtil.displayFilesize(bytesPerSecondMax) +
                " bpsa:" + StringUtil.displayFilesize(bytesPerSecondAverage);
    }

    //    @Override
//    public boolean equals(Object o) {
////        return super.equals(o);
//        if (o instanceof Download) {
//            return ((Download) o).uri.equals(uri) && ((Download) o).targetDir.equals(targetDir);
//        }
//        return false;
//    }
}
