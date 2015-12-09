package com.mricefox.mfdownloader.lib;

import android.os.Handler;

import com.mricefox.mfdownloader.lib.assist.MFLog;
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
    private Persistence<Download> persistence;

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
        MFLog.setDebugState(configuration.isDebuggable());
    }

    public long enqueue(Download download) {//todo enqueue same target file path download
        return downloadConsumerExecutor.startDownload(download);
    }

    public void pause(long id) {
        downloadConsumerExecutor.setDownloadPaused(id);
        MFLog.d("pause id:" + id);
    }

    public void resume(long id, DownloadingListener listener) {
        Download download = persistence.query(id);
        if (download == null)
            throw new IllegalArgumentException("can not find download");
        if (download.getStatus() != Download.STATUS_PAUSED)
            throw new IllegalArgumentException("can not resume a not paused download");
        download.setDownloadingListener(listener);
//        MFLog.d("resume total:"+wrapper.getTotalBytes());
        MFLog.d("resume id:" + id);
        downloadConsumerExecutor.resumeDownload(download);
    }

    public void cancel(long id, boolean deleteFile, DownloadingListener listener) {
        Download download = persistence.query(id);
        if (download == null)
            throw new IllegalArgumentException("can not find download");
        if (download.getStatus() == Download.STATUS_SUCCESSFUL) {
            throw new IllegalArgumentException("can not cancel a not successful download");
        }
        download.setDownloadingListener(listener);
        MFLog.d("cancel id:" + id);
    }

    private void runTask(Handler handler, Runnable r) {
        if (handler == null) {
            r.run();
        } else {
            handler.post(r);
        }
    }

    private Contract ConsumerContract = new Contract() {

        @Override
        public long insertDownload(Download download) {
            return persistence.insert(download);
        }

        @Override
        public long updateDownload(Download download) {
            return persistence.update(download);
        }

        @Override
        public List<Download> queryAll() {
            return persistence.queryAll();
        }

        @Override
        public void triggerAddEvent(final Download download) {
            final DownloadingListener listener = download.getDownloadingListener();
            if (listener != null) {
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
                        listener.onAdded(download.getId());
//                    }
//                };
//                runTask(wrapper.getDownload().getOptions().getHandler(), r);
            }
        }

        @Override
        public void triggerStartEvent(Download download) {
            DownloadingListener listener = download.getDownloadingListener();
            if (listener != null) listener.onStart(download.getId());
        }

        @Override
        public void triggerFailEvent(Download download) {
            DownloadingListener listener = download.getDownloadingListener();
            if (listener != null) listener.onFailed(download.getId());
        }

        @Override
        public void triggerProgressEvent(Download download) {
            DownloadingListener listener = download.getDownloadingListener();
            if (listener != null)
                listener.onProgressUpdate(download.getId(), download.getCurrentBytes(), download.getTotalBytes(), 0);
        }

        @Override
        public void triggerCompleteEvent(Download download) {
            DownloadingListener listener = download.getDownloadingListener();
            if (listener != null) listener.onComplete(download.getId());
        }

        @Override
        public void triggerPauseEvent(Download download) {
            DownloadingListener listener = download.getDownloadingListener();
            if (listener != null) listener.onPaused(download.getId());
        }

        @Override
        public Download queryFirstPendingDownload() {
            List<Download> all = persistence.queryAll();
            if (all == null || all.size() == 0) {
                MFLog.d("query all fail");
                return null;
            }
            Collections.sort(all, new Comparator<Download>() {//sort by priority desc
                @Override
                public int compare(Download lhs, Download rhs) {
//                    int lp = lhs.getDownload().getPriority();
//                    int rp = rhs.getDownload().getOptions().getPriority();
                    // TODO: 2015/12/8
                    int lp = 0;
                    int rp = 0;
                    if (lp > rp) return -1;
                    else if (lp < rp) return 1;
                    else return 0;
                }
            });
            for (int i = 0, size = all.size(); i < size; ++i) {
                Download download = all.get(i);
                MFLog.d("download.getStatus():" + download.getStatus() + "#id:" + download.getId());
                if (download.getStatus() == Download.STATUS_PENDING)
                    return download;
            }
            return null;
        }
    };
}
