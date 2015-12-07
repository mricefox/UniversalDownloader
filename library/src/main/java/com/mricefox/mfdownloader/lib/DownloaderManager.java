package com.mricefox.mfdownloader.lib;

import com.mricefox.mfdownloader.lib.assist.L;
import com.mricefox.mfdownloader.lib.persistence.Persistence;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                new DownloadConsumerExecutor(configuration.getDownloadOperator(), ConsumerContract,
                        configuration.getMaxDownloadNum(), configuration.isAutoStartPending());
        persistence = configuration.getPersistence();
        L.setDebugState(configuration.isDebuggable());
    }

    public long enqueue(Download download) {//todo enqueue same target file path download
        DownloadWrapper wrapper = new DownloadWrapper(download);
        return downloadConsumerExecutor.startDownload(wrapper);
    }

    public void pause(long id) {
        downloadConsumerExecutor.setDownloadPaused(id);
        L.d("pause id:" + id);
    }

    public void resume(long id, DownloadingListener listener) {
        DownloadWrapper wrapper = persistence.query(id);
        if (wrapper == null)
            throw new IllegalArgumentException("can not find download");
        if (wrapper.getStatus() != Download.STATUS_PAUSED)
            throw new IllegalArgumentException("can not resume a not paused download");
        wrapper.getDownload().setDownloadingListener(listener);
//        L.d("resume total:"+wrapper.getTotalBytes());
        L.d("resume id:" + id);
        downloadConsumerExecutor.resumeDownload(wrapper);
    }

    public void cancel(long id, boolean deleteFile, DownloadingListener listener) {
        DownloadWrapper wrapper = persistence.query(id);
        if (wrapper == null)
            throw new IllegalArgumentException("can not find download");
        if (wrapper.getStatus() == Download.STATUS_SUCCESSFUL) {
            throw new IllegalArgumentException("can not cancel a not successful download");
        }
        wrapper.getDownload().setDownloadingListener(listener);
        L.d("cancel id:" + id);
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

        @Override
        public List<DownloadWrapper> queryAll() {
            return persistence.queryAll();
        }

        @Override
        public void fireAddEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null) listener.onAdded(wrapper.getDownload().getId());
        }

        @Override
        public void fireStartEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null) listener.onStart(wrapper.getDownload().getId());
        }

        @Override
        public void fireFailEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null) listener.onFailed(wrapper.getDownload().getId());
        }

        @Override
        public void fireProgressEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null)
                listener.onProgressUpdate(wrapper.getDownload().getId(), wrapper.getCurrentBytes(), wrapper.getTotalBytes(), 0);
        }

        @Override
        public void fireCompleteEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null) listener.onComplete(wrapper.getDownload().getId());
        }

        @Override
        public void firePauseEvent(DownloadWrapper wrapper) {
            DownloadingListener listener = wrapper.getDownload().getDownloadingListener();
            if (listener != null) listener.onPaused(wrapper.getDownload().getId());
        }

        @Override
        public DownloadWrapper queryFirstPendingDownload() {
            List<DownloadWrapper> all = persistence.queryAll();
            if (all == null || all.size() == 0) {
                L.d("query all fail");
                return null;
            }
            Collections.sort(all, new Comparator<DownloadWrapper>() {//sort by priority desc
                @Override
                public int compare(DownloadWrapper lhs, DownloadWrapper rhs) {
                    int lp = lhs.getDownload().getPriority();
                    int rp = rhs.getDownload().getPriority();
                    if (lp > rp) return -1;
                    else if (lp < rp) return 1;
                    else return 0;
                }
            });
            for (int i = 0, size = all.size(); i < size; ++i) {
                DownloadWrapper wrapper = all.get(i);
                L.d("wrapper.getStatus():" + wrapper.getStatus() + "#id:" + wrapper.getDownload().getId());
                if (wrapper.getStatus() == Download.STATUS_PENDING)
                    return wrapper;
            }
            return null;
        }
    };
}
