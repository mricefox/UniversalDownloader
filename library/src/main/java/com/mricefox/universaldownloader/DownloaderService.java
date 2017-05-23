package com.mricefox.universaldownloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public class DownloaderService extends Service {
    private static final String TAG = "ud.service";

    private DownloaderServiceStub stub;

    public DownloaderService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        UDLogger.i(TAG, "onCreate");

        stub = new DownloaderServiceStub();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UDLogger.i(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        UDLogger.i(TAG, "onBind");
        return stub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        UDLogger.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    private class Dispatcher extends Thread {
        Dispatcher() {
            super("ud-dispatcher");
        }

        @Override
        public void run() {
            super.run();


        }
    }
}
