package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    public static final int STATUS_PENDING = 1 << 1;
    public static final int STATUS_RUNNING = 1 << 2;
    public static final int STATUS_PAUSED = 1 << 3;
    private String uri;
    private String targetFilePath;
    private DownloadingListener downloadingListener;


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

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

    public void setTargetFilePath(String targetFilePath) {
        this.targetFilePath = targetFilePath;
    }

    public DownloadingListener getDownloadingListener() {
        return downloadingListener;
    }

    public void setDownloadingListener(DownloadingListener downloadingListener) {
        this.downloadingListener = downloadingListener;
    }
}
