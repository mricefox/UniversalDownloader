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
    public static final int STATUS_SUCCESSFUL = 1 << 4;
    public static final int STATUS_FAILED = 1 << 5;
    private long id;
    private String uri;
    private String targetFilePath;
    private int priority;
    transient private DownloadingListener downloadingListener;

    public Download(String uri, String targetFilePath, int priority) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
        this.priority = priority;
    }

    public Download(String uri, String targetFilePath, long id, int priority) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
        this.id = id;
        this.priority = priority;
    }

    public Download(String uri, String targetFilePath, DownloadingListener listener, int priority) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
        downloadingListener = listener;
        this.priority = priority;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
//        return super.equals(o);
        if (o instanceof Download) {
            return ((Download) o).uri.equals(uri) && ((Download) o).targetFilePath.equals(targetFilePath);
        }
        return false;
    }
}
