package com.mricefox.universaldownloader;

import android.util.Log;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/23
 */

public class UDLogger {
    private static final boolean ENABLE = true;

    private UDLogger() {
    }

    public static void i(String tag, String msg) {
        if (ENABLE) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (ENABLE) {
            Log.i(tag, String.format(format, args));
        }
    }

    public static void w(String tag, String msg) {
        if (ENABLE) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String format, Object... args) {
        if (ENABLE) {
            Log.w(tag, String.format(format, args));
        }
    }
}
