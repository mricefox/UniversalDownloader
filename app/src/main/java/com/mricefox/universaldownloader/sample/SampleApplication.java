package com.mricefox.universaldownloader.sample;

import android.app.Application;

import com.mricefox.universaldownloader.UniversalDownloader;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/23
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        UniversalDownloader.inst().init(this);
    }
}
