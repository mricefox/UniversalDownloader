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

    void triggerAddEvent(Download download);

    void triggerStartEvent(Download download);

    void triggerFailEvent(Download download);

    void triggerProgressEvent(Download download);

    void triggerCompleteEvent(Download download);

    void triggerPauseEvent(Download download);

    void triggerCancelEvent(Download download);
}
