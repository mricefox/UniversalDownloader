package com.mricefox.mfdownloader.lib;


import android.os.Handler;
import android.os.Looper;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/7
 */
public class DownloadOptions {
    private Handler handler;
    private int priority;

    private DownloadOptions() {
    }

    private DownloadOptions(Builder builder) {
        handler = builder.handler;
        priority = builder.priority;
        //define a ui handler if constructor run in ui thread
        if (handler == null && Looper.myLooper() == Looper.getMainLooper()) handler = new Handler();
    }

    public static DownloadOptions createDefaultOptions() {
        return new DownloadOptions();
    }

    public Handler getHandler() {
        return handler;
    }

    public int getPriority() {
        return priority;
    }

    public static class Builder {
        private Handler handler;
        private int priority;

        public Builder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public DownloadOptions build() {
            return new DownloadOptions(this);
        }
    }
}
