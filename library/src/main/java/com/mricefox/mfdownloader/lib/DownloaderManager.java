package com.mricefox.mfdownloader.lib;

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
        downloadConsumerExecutor.submitDownload(download);

        return -1L;
    }
}
