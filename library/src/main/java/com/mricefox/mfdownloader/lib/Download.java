package com.mricefox.mfdownloader.lib;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    public static final int STATUS_PENDING = 1 << 1;
    public static final int STATUS_RUNNING = 1 << 2;
    public static final int STATUS_PAUSED = 1 << 3;
    private final String uri;
    private final String targetFilePath;
    private DownloadingListener downloadingListener;

    private int status = -1;
//    private long id;

    public Download(String uri, String targetFilePath) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
    }

    public Download(String uri, String targetFilePath, DownloadingListener listener) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
        downloadingListener = listener;
    }

    public String getUri() {
        return uri;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public DownloadingListener getDownloadingListener() {
        return downloadingListener;
    }

//    public long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

//    public int getStatus() {
//        return status.get();
//    }
//
//    public void setStatus(int status) {
//        this.status.set(status);
//    }
}
