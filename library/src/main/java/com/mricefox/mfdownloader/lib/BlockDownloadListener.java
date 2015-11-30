package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/27
 */
interface BlockDownloadListener {
    /**
     * @param downloadId    download id
     * @param blockId       block id
     * @param current       Loaded bytes
     * @param total         Total bytes for loading
     * @param bytesThisTime bytes count this download
     * @return <b>true</b> - if copying should be continued; <b>false</b> - if copying should be interrupted
     */
    boolean onBytesDownload(long downloadId, int blockId, long current, long total, long bytesThisTime);

    void onComplete(long downloadId, int blockId);
}
