package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/2
 */
public interface Contract {
    long insertDownload(DownloadWrapper wrapper);

    long updateDownload(DownloadWrapper wrapper);
}
