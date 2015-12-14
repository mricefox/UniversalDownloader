package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/2
 */
public interface Contract {
    long insertDownload(Download download);

    long updateDownload(Download download);

    List<Download> queryAll();

    Download queryFirstPendingDownload();

    long deleteDownload(Download download);

    void notifyDownloadObserver(Download download);


}
