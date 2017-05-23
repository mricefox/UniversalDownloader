package com.mricefox.universaldownloader;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public final class UniversalDownloader {
    private static final String TAG = "ud.downloader";
    private Context appContext;
    private AtomicBoolean inited = new AtomicBoolean();

    private static final class InstanceHolder {
        private static final UniversalDownloader INSTANCE = new UniversalDownloader();
    }

    private UniversalDownloader() {
    }

    public static UniversalDownloader inst() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context context) {
        if (!inited.get()) {
            this.appContext = context.getApplicationContext();
            DownloaderServiceProxy.inst().init(appContext);
            inited.set(true);
        } else {
            UDLogger.w(TAG, "Already inited!");
        }
    }

    public DownloadTask create(Uri uri) {
        return new DownloadTask(uri);
    }

    public static class GlobalConfiguration {
        public static final int DEFAULT_PARELLEL_NUM = 3;

        private int parellelNum;
        private DownloaderConnection connection;

    }

}
