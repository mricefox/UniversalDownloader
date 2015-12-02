package com.mricefox.mfdownloader.lib;

import com.mricefox.mfdownloader.lib.persistence.Persistence;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class DownloaderManager {
    private static DownloaderManager instance;
    private DownloadConsumerExecutor downloadConsumerExecutor;
    private Persistence<DownloadWrapper> persistence;

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
                new DownloadConsumerExecutor(configuration.getMaxDownloadNum(), configuration.getDownloadOperator(), ConsumerContract);
        persistence = configuration.getPersistence();
    }

    public long enqueue(Download download) {//todo enqueue same target file path download
        DownloadWrapper wrapper = new DownloadWrapper(download, -1);
        return downloadConsumerExecutor.addDownload(wrapper);
    }

    public void pause(long id) {
        downloadConsumerExecutor.setDownloadPaused(id);
    }

    public void resume(long id) {
        DownloadWrapper wrapper = persistence.query(id);
        if (wrapper == null || wrapper.getStatus() != Download.STATUS_PAUSED)
            throw new IllegalArgumentException("can not resume a not paused download");
        downloadConsumerExecutor.resumeDownload(wrapper);
    }

    public void cancel(long id) {

    }

    private Contract ConsumerContract = new Contract() {

        @Override
        public long insertDownload(DownloadWrapper wrapper) {
            return persistence.insert(wrapper);
        }

        @Override
        public long updateDownload(DownloadWrapper wrapper) {
            return persistence.update(wrapper);
        }
    };
}
