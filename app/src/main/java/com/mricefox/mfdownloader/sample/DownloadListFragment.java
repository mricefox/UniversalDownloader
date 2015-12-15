package com.mricefox.mfdownloader.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloadListener;
import com.mricefox.mfdownloader.lib.DownloadObserver;
import com.mricefox.mfdownloader.lib.DownloadParams;
import com.mricefox.mfdownloader.lib.DownloaderManager;
import com.mricefox.mfdownloader.lib.assist.MFLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 */
public class DownloadListFragment extends Fragment {
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp";
    private RecyclerView downloadRecyclerView;
    private DownloadListAdapter downloadListAdapter;
    private List<Download> downloadList = new ArrayList();
    private List<Download> todoDownloadList = new ArrayList<>();

    public DownloadListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DownloadListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadListFragment newInstance(String param1, String param2) {
        DownloadListFragment fragment = new DownloadListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download_list, container, false);
        downloadRecyclerView = (RecyclerView) v.findViewById(R.id.download_list);


        DownloaderManager.getInstance().registerObserver(observer);
        downloadListAdapter = new DownloadListAdapter(downloadList, getActivity());
        downloadRecyclerView.setHasFixedSize(true);
        downloadListAdapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        downloadRecyclerView.setLayoutManager(layoutManager);
        downloadRecyclerView.setAdapter(downloadListAdapter);

        initDownload();

        downloadListAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MFLog.d("onItemClick#pos:" + position);
                Download download = downloadList.get(position);
//                synchronized (download) {
                int status = download.getStatus();
                switch (status) {
                    case Download.STATUS_RUNNING:
                        DownloaderManager.getInstance().pause(id);
//                        MFLog.d("pause download:" + download);
                        break;
                    case Download.STATUS_PAUSED:
                        Download d = DownloaderManager.getInstance().resume(id);
                        if (d != null)
                            downloadList.set(position, d);
                        break;
                }
//                }
                downloadListAdapter.notifyItemChanged(position);
            }
        });

        downloadListAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                MFLog.d("onItemLongClick#pos:" + position);
                Download download = downloadList.get(position);
                if (download.getStatus() == Download.STATUS_SUCCESSFUL) {

                }
                final Dialog dialog = new AlertDialog.Builder(getActivity()).
                        setTitle("confirm cancel download ?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DownloaderManager.getInstance().cancel(id);
                        downloadList.remove(position);
                        downloadListAdapter.notifyItemRemoved(position);
                    }
                }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
                dialog.show();
                return false;
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initDownload() {
        Download download1 = new Download(new DownloadParams(SampleUris.SampleUri1, TargetDir));
        Download download2 = new Download(new DownloadParams(SampleUris.SampleUri2, TargetDir));
        Download download3 = new Download(new DownloadParams(SampleUris.SampleUri3, TargetDir));
        Download download4 = new Download(new DownloadParams(SampleUris.SampleUri4, TargetDir));
        Download download5 = new Download(new DownloadParams(SampleUris.SampleUri5, TargetDir));
        Download download6 = new Download(new DownloadParams(SampleUris.SampleUri6, TargetDir));
        Download download7 = new Download(new DownloadParams(SampleUris.SampleUri7, TargetDir));
        Download download8 = new Download(new DownloadParams(SampleUris.SampleUri8, TargetDir));
        todoDownloadList.add(download1);
        todoDownloadList.add(download2);
        todoDownloadList.add(download3);
        todoDownloadList.add(download4);
        todoDownloadList.add(download5);
        todoDownloadList.add(download6);
        todoDownloadList.add(download7);
        todoDownloadList.add(download8);
    }

    public void startNextDownload() {
        Download d = todoDownloadList.remove(0);
        DownloaderManager.getInstance().enqueue(d);
        MFLog.d("startNextDownload d:" + d);
        downloadList.add(d);
        downloadListAdapter.notifyItemInserted(downloadList.size() - 1);
    }

    private boolean isItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) downloadRecyclerView.getLayoutManager();
        if (position >= layoutManager.findFirstVisibleItemPosition() &&
                position <= layoutManager.findLastVisibleItemPosition()) return true;
        return false;
    }

    private DownloadObserver observer = new DownloadObserver() {
        @Override
        public void onChanged(Download download) {
            final long id = download.getId();
            new android.os.Handler(Looper.getMainLooper()).post(new UpdateRecyclerViewTask(id));
        }
    };

    private class UpdateRecyclerViewTask implements Runnable {
        private long id;

        private UpdateRecyclerViewTask(long id) {
            this.id = id;
        }

        @Override
        public void run() {
            RecyclerView.ViewHolder holder = downloadRecyclerView.findViewHolderForItemId(id);
            if (holder != null) {
                int position = holder.getAdapterPosition();
//                MFLog.d("UpdateRecyclerViewTask position:" + position);
                downloadListAdapter.notifyItemChanged(position);
            } else MFLog.e("holder==null");
        }
    }

    private DownloadListener downloadingListener = new DownloadListener() {

        @Override
        public void onAdded(long id) {
            MFLog.d("id:" + id + "#onAdded");
        }

        @Override
        public void onStart(long id) {
            MFLog.d("id:" + id + "#onStart");
        }

        @Override
        public void onComplete(long id) {
            MFLog.d("id:" + id + "#onComplete");
        }

        @Override
        public void onFailed(long id) {
            MFLog.d("id:" + id + "#onFailed");
        }

        @Override
        public void onCancelled(long id) {
            MFLog.d("id:" + id + "#onCancelled");
        }

        @Override
        public void onPaused(long id) {
            MFLog.d("id:" + id + "#onPaused");
        }

        @Override
        public void onProgressUpdate(long id, long current, long total, long bytesPerSecond) {
            MFLog.d("id:" + id + "#onProgressUpdate" + "#current" + current + "#total" +
                    total + "#%" + String.format("%.2f", (current + 0.0f) * 100 / total));
        }
    };
}
