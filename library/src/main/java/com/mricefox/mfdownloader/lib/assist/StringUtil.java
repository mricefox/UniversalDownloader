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
}
