package com.mricefox.mfdownloader.lib;

import android.os.Handler;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/9
 */
public class DownloadParams {
    private final String uri;
    private final String targetDir;

    private String fileName;
    private int priority;
    private Handler callbackHandler;

    public DownloadParams(String uri, String targetDir) {
        this.uri = uri;
        this.targetDir = targetDir;

        // TODO: 2015/12/15  
//        String scheme = uri.getScheme();
//        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
//            throw new IllegalArgumentException("Can only download HTTP/HTTPS URIs: " + uri);
//        }
    }

    public DownloadParams fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DownloadParams priority(int priority) {
        this.priority = priority;
        return this;
    }

    public DownloadParams callbackHandler(Handler handler) {
        this.callbackHandler = handler;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public String getFileName() {
        return fileName;
    }

    public int getPriority() {
        return priority;
    }

    public Handler getCallbackHandler() {
        return callbackHandler;
    }
}