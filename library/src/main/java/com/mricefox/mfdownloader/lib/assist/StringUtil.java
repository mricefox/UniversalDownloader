package com.mricefox.mfdownloader.lib.assist;

import java.text.DecimalFormat;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/10
 */
public class StringUtil {
    private StringUtil() {
    }

    public static String displayFilesize(long fileSize) {
        if (fileSize <= 0) {
            return "0";
        } else {
            final String[] fileUnit = new String[]{"B", "KB", "MB", "GB", "TB"};
            int group = (int) (Math.log10(fileSize) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(fileSize
                    / Math.pow(1024, group)) + fileUnit[group];
        }
    }

    /**
     * convert millisecond to hh:mm:ss
     * @param ms
     * @return
     */
    public static String convertMills2hhmmss(long ms) {
        long time;
        int second, minute, hour;
        time = ms / 1000;
        second = (int) (time % 60);
        minute = (int) (time / 60 % 60);
        hour = (int) (time / 3600);
        return (hour < 10 ? "0" : "") + hour
                + ":" + (minute < 10 ? "0" : "") + minute
                + ":" + (second < 10 ? "0" : "") + second;
    }
}
