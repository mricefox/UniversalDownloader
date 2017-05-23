package com.mricefox.universaldownloader;

import android.net.Uri;

import java.io.File;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public class DownloadTask {
    private final Uri uri;
    private File targetDir;
    private String fileName;
    private Callback callback;
    private boolean multiThread;

    DownloadTask(Uri uri) {
        this.uri = uri;
    }

    public File getTargetDir() {
        return targetDir;
    }

    public DownloadTask setTargetDir(File targetDir) {
        this.targetDir = targetDir;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public DownloadTask setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Callback getCallback() {
        return callback;
    }

    public DownloadTask setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public boolean isMultiThread() {
        return multiThread;
    }

    public DownloadTask setMultiThread(boolean multiThread) {
        this.multiThread = multiThread;
        return this;
    }

    public int enqueue() {
        DownloaderServiceProxy.inst().enqueue(new DownloadTask(null));
        return -1;
    }

    public void pause(int id) {

    }

    public void stop(int id) {

    }

    public void resume(int id) {

    }


    public interface Callback {
        void onStart(int id);

        void onPaused(int id);

        void onProgressUpdate(long id, long current, long total, long bytesPerSecond);

        void onComplete(long id);

        void onFailed(long id);

        void onCancelled(long id);
    }
}
