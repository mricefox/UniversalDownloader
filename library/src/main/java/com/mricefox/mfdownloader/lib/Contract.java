package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/2
 */
public interface Contract {
    long insertDownload(DownloadWrapper wrapper);

    long updateDownload(DownloadWrapper wrapper);

    List<DownloadWrapper> queryAll();

    DownloadWrapper queryFirstPendingDownload();

    void fireAddEvent(DownloadWrapper wrapper);

    void fireStartEvent(DownloadWrapper wrapper);

    void fireFailEvent(DownloadWrapper wrapper);

    void fireProgressEvent(DownloadWrapper wrapper);

    void fireCompleteEvent(DownloadWrapper wrapper);

    void firePauseEvent(DownloadWrapper wrapper);
}
