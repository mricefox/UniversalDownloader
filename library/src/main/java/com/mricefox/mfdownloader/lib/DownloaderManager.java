package com.mricefox.mfdownloader.lib;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class DownloaderManager {
    private DownloadConsumerExecutor downloadConsumerExecutor;

    public DownloaderManager(Configuration configuration) {
        downloadConsumerExecutor =
                new DownloadConsumerExecutor(configuration.getMaxDownloadNum(), configuration.getDownloadOperator());
    }

    public long enqueue(Download download) {
        DownloadWrapper wrapper = new DownloadWrapper();
        wrapper.download = download;
        return downloadConsumerExecutor.addDownload(wrapper);
    }

    public void pause(long id) {

    }

    public void resume(long id) {

    }

    public void cancel(long id) {

    }
}
