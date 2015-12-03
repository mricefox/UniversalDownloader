package com.mricefox.mfdownloader.lib.operator;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/27
 */
public interface BlockDownloadListener {
    /**
     * @param downloadId    download id
     * @param blockIndex    block index
     * @param current       Loaded bytes
     * @param total         Total bytes for loading
     * @param bytesThisStep bytes count this download
     * @return <b>true</b> - if copying should be continued; <b>false</b> - if copying should be interrupted
     */
    boolean onBytesDownload(long downloadId, int blockIndex, long current, long total, long bytesThisStep);

    void onDownloadStop(long downloadId, int blockIndex, long currentBytes);

    void onDownloadFail(long downloadId, int blockIndex, long currentBytes);
}
