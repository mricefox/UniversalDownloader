package com.mricefox.mfdownloader.lib;

import android.os.Handler;
import android.os.Looper;

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

    //user config attrs
    private final String uri;
    private final String targetFilePath;
    private final int priority;
    private final DownloadListener downloadListener;
    private final Handler callbackHandler;

    private long id;
    private List<Block> blocks = new ArrayList<>();
    private long totalBytes;
    private long currentBytes;
    private int status;

    public Download(DownloadParams params) {
        this.uri = params.getUri();
        this.targetFilePath = params.getTargetFilePath();
        this.priority = params.getPriority();
        this.downloadListener = params.getDownloadListener();
        Handler h = params.getCallbackHandler();

        //define a ui handler if constructor run in ui thread
        if (h == null && Looper.myLooper() == Looper.getMainLooper())
            this.callbackHandler = new Handler();
        else
            this.callbackHandler = h;

    }

//    public Download(String uri, String targetFilePath, DownloadListener listener) {
//        this.uri = uri;
//        this.targetFilePath = targetFilePath;
//        this.downloadListener = listener;
//    }

    public String getUri() {
        return uri;
    }

//    public void setUri(String uri) {
//        this.uri = uri;
//    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

//    public void setTargetFilePath(String targetFilePath) {
//        this.targetFilePath = targetFilePath;
//    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

//    public void setDownloadingListener(DownloadListener downloadListener) {
//        this.downloadListener = downloadListener;
//    }

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

    //    @Override
//    public boolean equals(Object o) {
////        return super.equals(o);
//        if (o instanceof Download) {
//            return ((Download) o).uri.equals(uri) && ((Download) o).targetFilePath.equals(targetFilePath);
//        }
//        return false;
//    }
}
