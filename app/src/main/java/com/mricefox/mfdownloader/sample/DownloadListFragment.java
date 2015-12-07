package com.mricefox.mfdownloader.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloaderManager;
import com.mricefox.mfdownloader.lib.DownloadingListener;
import com.mricefox.mfdownloader.lib.assist.L;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DownloadListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String SampleUri1 = "http://dldir1.qq.com/qqfile/qq/QQ7.8/16379/QQ7.8.exe";
    private final static String SampleUri2 = "http://sqdd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private final static String SampleUri3 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271444197407.zip";
    private final static String SampleUri4 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271438127289.zip";
    private final static String SampleUri5 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271434586180.zip";
    private final static String SampleUri6 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271423353240.zip";
    private final static String SampleUri7 = "http://i2.dpfile.com/download/Dianping_dianping-m_794.apk";
    private final static String SampleUri8 = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp";
    private DownloadingListener downloadingListener = new DownloadingListener() {

        @Override
        public void onAdded(long id) {
            L.d("id:" + id + "#onAdded");
        }

        @Override
        public void onStart(long id) {
            L.d("id:" + id + "#onStart");
        }

        @Override
        public void onComplete(long id) {
            L.d("id:" + id + "#onComplete");
        }

        @Override
        public void onFailed(long id) {
            L.d("id:" + id + "#onFailed");
        }

        @Override
        public void onCancelled(long id) {
            L.d("id:" + id + "#onCancelled");
        }

        @Override
        public void onPaused(long id) {
            L.d("id:" + id + "#onPaused");
        }

        @Override
        public void onProgressUpdate(long id, long current, long total, long bytesPerSecond) {
//            L.d("id:" + id + "#onProgressUpdate" + "#current" + current + "#total" +
//                    total + "#%" + String.format("%.2f", (current + 0.0f) * 100 / total));
            int position = downloadIdMap.get(id);
            DownloadListAdapter.ItemViewHolder holder =
                    (DownloadListAdapter.ItemViewHolder) downloadRecyclerView.findViewHolderForAdapterPosition(position);

            holder.progressBar.setProgress((int) ((current + 0.0f) / total * 100));

//            Iterator<Integer> iterator = holder.bindPositionSet.iterator();
//
//            while (iterator.hasNext()) {
//                int pos = iterator.next();
//                L.d(holder + "bind pos:" + pos);
//                if (isItemVisible(pos)) {
//
//                }
//
//            }


//            }
        }

    };

    Download download1 = new Download(SampleUri3, TargetDir + File.separator + "novel1.zip", downloadingListener, 0);
    Download download2 = new Download(SampleUri4, TargetDir + File.separator + "novel2.zip", downloadingListener, 0);
    Download download3 = new Download(SampleUri5, TargetDir + File.separator + "novel3.zip", downloadingListener, 1);
    Download download4 = new Download(SampleUri6, TargetDir + File.separator + "novel4.zip", downloadingListener, 6);
    Download download5 = new Download(SampleUri2, TargetDir + File.separator + "qq.apk", downloadingListener, 3);
    Download download6 = new Download(SampleUri1, TargetDir + File.separator + "qq.exe", downloadingListener, 2);
    Download download7 = new Download(SampleUri7, TargetDir + File.separator + "Dianping_dianping-m_794.apk", downloadingListener, 4);
    Download download8 = new Download(SampleUri8, TargetDir + File.separator + "MobileAssistant_1.apk", downloadingListener, 5);

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView downloadRecyclerView;
    private DownloadListAdapter downloadListAdapter;
    private List<Download> downloadList = new ArrayList<>();

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
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download_list, container, false);
        downloadRecyclerView = (RecyclerView) v.findViewById(R.id.download_list);
        downloadListAdapter = new DownloadListAdapter(downloadList, getActivity());
//        DownloaderManager.getInstance().enqueue(download1);
//        DownloaderManager.getInstance().enqueue(download2);
//        DownloaderManager.getInstance().enqueue(download3);
//        DownloaderManager.getInstance().enqueue(download4);
//        DownloaderManager.getInstance().enqueue(download5);
//        DownloaderManager.getInstance().enqueue(download6);
//        DownloaderManager.getInstance().enqueue(download7);
//        DownloaderManager.getInstance().enqueue(download8);
        downloadList.add(download1);
        downloadList.add(download2);
        downloadList.add(download3);
        downloadList.add(download4);
        downloadList.add(download5);
        downloadList.add(download6);
        downloadList.add(download7);
        downloadList.add(download8);

        downloadRecyclerView.setHasFixedSize(true);
//        adapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        downloadRecyclerView.setLayoutManager(layoutManager);
        downloadRecyclerView.setAdapter(downloadListAdapter);

        downloadListAdapter.setOnItemClickListener(new DownloadListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                L.d("onItemClick#pos:" + position);
                long d_id = DownloaderManager.getInstance().enqueue(downloadList.get(position));
//                L.d("onItemClick#d_id:" + d_id);
                downloadIdMap.put(d_id, position);

                L.d("onItemClick#getId:" + downloadList.get(position).getId());
                RecyclerView.ViewHolder holder = downloadRecyclerView.findViewHolderForAdapterPosition(position);

                L.d("onItemClick#holder:" + holder);
            }
        });
        return v;
    }

    private Map<Long, Integer> downloadIdMap = new HashMap<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private boolean isItemVisible(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) downloadRecyclerView.getLayoutManager();
        if (position >= layoutManager.findFirstVisibleItemPosition() &&
                position <= layoutManager.findLastVisibleItemPosition()) return true;
        return false;
    }

}
