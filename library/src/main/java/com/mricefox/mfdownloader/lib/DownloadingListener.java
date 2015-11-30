package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/30
 */
public interface DownloadingListener {
    void onStart(long id);

    void onComplete(long id);

    void onFailed(long id);

    void onCancelled(long id);

    void onPaused(long id);

    void onProgressUpdate(long id, long current, long total, long bytesPerSecond);
}
