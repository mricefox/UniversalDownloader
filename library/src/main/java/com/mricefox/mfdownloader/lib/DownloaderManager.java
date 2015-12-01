package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class DownloaderManager {
    private static DownloaderManager instance;
    private DownloadConsumerExecutor downloadConsumerExecutor;

    private DownloaderManager() {
    }

    public static DownloaderManager getInstance() {
        if (instance == null) {
            synchronized (DownloaderManager.class) {
                if (instance == null) instance = new DownloaderManager();
            }
        }
        return instance;
    }

    public synchronized void init(Configuration configuration) {
        downloadConsumerExecutor =
                new DownloadConsumerExecutor(configuration.getMaxDownloadNum(), configuration.getDownloadOperator());
    }

    public long enqueue(Download download) {
        DownloadWrapper wrapper = new DownloadWrapper();
        wrapper.download = download;
        return downloadConsumerExecutor.addDownload(wrapper);
    }

    public void pause(long id) {
        downloadConsumerExecutor.setDownloadPaused(id);
    }

    public void resume(long id) {
    }

    public void cancel(long id) {

    }
}
