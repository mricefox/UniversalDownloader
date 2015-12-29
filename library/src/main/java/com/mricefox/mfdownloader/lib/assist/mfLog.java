package com.mricefox.mfdownloader.lib.assist;

import android.text.TextUtils;
import android.util.Log;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/27
 */
public class MFLog {
    private static boolean DEBUG = true;
    private static final String TAG = "mf_download";

    private static String className;
    private static String methodName;
    private static int lineNumber;

    private MFLog() {
    }

    public static void setDebugState(boolean enable) {
        DEBUG = enable;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(className);
        buffer.append("[");
        buffer.append(methodName);
        buffer.append(":");
        buffer.append(lineNumber);
        buffer.append("]");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void d(String tag, String logContent) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }

        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(tag, createLog(logContent));
        }
    }

    public static void e(String tag, String logContent, Throwable t) {
        if (TextUtils.isEmpty(tag))
            tag = TAG;
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(tag, createLog(logContent), t);
        }
    }

    public static void e(String tag, String logContent) {
        if (TextUtils.isEmpty(tag))
            tag = TAG;
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(tag, createLog(logContent));
        }
    }

    public static void e(String logContent) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(TAG, createLog(logContent));
        }
    }

    public static void e(String logContent, Throwable t) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(TAG, createLog(logContent), t);
        }
    }

    public static void d(String logContent) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(TAG, createLog(logContent));
        }
    }

    public static void i(String logContent) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(TAG, createLog(logContent));
        }
    }

    public static void w(String logContent) {
        if (DEBUG) {
            getMethodNames(new Throwable().getStackTrace());
            Log.w(TAG, createLog(logContent));
        }
    }
}
