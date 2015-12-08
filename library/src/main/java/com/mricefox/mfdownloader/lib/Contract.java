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

    void triggerAddEvent(DownloadWrapper wrapper);

    void triggerStartEvent(DownloadWrapper wrapper);

    void triggerFailEvent(DownloadWrapper wrapper);

    void triggerProgressEvent(DownloadWrapper wrapper);

    void triggerCompleteEvent(DownloadWrapper wrapper);

    void triggerPauseEvent(DownloadWrapper wrapper);
}
