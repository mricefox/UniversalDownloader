package com.mricefox.mfdownloader.lib;

import android.database.Observable;
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
    private DownloadObservable downloadObservable;

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
        downloadObservable = new DownloadObservable();
    }

    public long enqueue(Download download) {//todo enqueue same target file path download
        download.reset();//in case of same object pass in
        return downloadConsumerExecutor.startDownload(download);
    }

    /**
     * paused a downloading, a download is not running can not be paused.
     *
     * @param id
     */
    public void pause(long id) {
        downloadConsumerExecutor.setDownloadPaused(id);
        MFLog.d("pause id:" + id);
    }

    public void resume(long id) {
        Download download = persistence.query(id);
        if (download == null)
            throw new IllegalArgumentException("can not find download");
        if (download.getStatus() != Download.STATUS_PAUSED
                && download.getStatus() != Download.STATUS_RUNNING)//paused or interrupt
            throw new IllegalArgumentException("can not resume download");
//        download.setDownloadingListener(listener);
//        MFLog.d("resume total:"+wrapper.getTotalBytes());
        MFLog.d("resume id:" + id);
        downloadConsumerExecutor.resumeDownload(download);
    }

    public void cancel(long id) {
        Download download = persistence.query(id);
        if (download == null)
            throw new IllegalArgumentException("can not find download");
        if (download.getStatus() == Download.STATUS_SUCCESSFUL) {
            throw new IllegalArgumentException("can not cancel a not successful download");
        }
//        download.setDownloadingListener(listener);
        downloadConsumerExecutor.cancelDownload(download);
        MFLog.d("cancel id:" + id);
    }

    public void registerObserver(DownloadObserver observer) {
        downloadObservable.registerObserver(observer);
    }

    public void unregisterObserver(DownloadObserver observer) {
        downloadObservable.unregisterObserver(observer);
    }

    public void unregisterAll() {
        downloadObservable.unregisterAll();
    }

    private void runTask(Handler handler, Runnable r) {
        if (handler == null) {
            r.run();
        } else {
            handler.post(r);
        }
    }

    /**
     * contact with executor
     */
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
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onAdded(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerStartEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onStart(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerFailEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerProgressEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onProgressUpdate(download.getId(), download.getCurrentBytes(), download.getTotalBytes(), 0);
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerCompleteEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerPauseEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onPaused(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
        }

        @Override
        public void triggerCancelEvent(final Download download) {
            final DownloadListener listener = download.getDownloadListener();
            if (listener != null) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        listener.onCancelled(download.getId());
                    }
                };
                runTask(download.getCallbackHandler(), r);
            }
            if (downloadObservable.hasObservers()) {
                downloadObservable.notifyChanged(download);
            }
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
                    int lp = lhs.getPriority();
                    int rp = rhs.getPriority();
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

        @Override
        public long deleteDownload(Download download) {
            return persistence.delete(download);
        }
    };

    private class DownloadObservable extends Observable<DownloadObserver> {
        public boolean hasObservers() {
            synchronized (mObservers) {
                return !mObservers.isEmpty();
            }
        }

        public void notifyChanged(Download download) {
            int size = 0;
            DownloadObserver[] arrays = null;
            synchronized (mObservers) {//mObservers register and notify maybe in different thread
                size = mObservers.size();
                arrays = new DownloadObserver[size];
                mObservers.toArray(arrays);
            }
            if (arrays != null) {
                for (int i = size - 1; i >= 0; i--) {
                    arrays[i].onChanged(download);
                }
            }
        }
    }
}
