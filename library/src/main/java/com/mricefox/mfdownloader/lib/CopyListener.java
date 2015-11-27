package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/27
 */
public interface CopyListener {
    /**
     * @param current Loaded bytes
     * @param total   Total bytes for loading
     * @return <b>true</b> - if copying should be continued; <b>false</b> - if copying should be interrupted
     */
    boolean onBytesCopied(int current, int total);
}
