package com.mricefox.mfdownloader.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mricefox.mfdownloader.lib.Configuration;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloadListener;
import com.mricefox.mfdownloader.lib.DownloadObserver;
import com.mricefox.mfdownloader.lib.DownloadParams;
import com.mricefox.mfdownloader.lib.DownloaderManager;
import com.mricefox.mfdownloader.lib.assist.MFLog;
import com.mricefox.mfdownloader.lib.assist.StorageUtils;
import com.mricefox.mfdownloader.lib.assist.StringUtil;
import com.mricefox.mfdownloader.lib.operator.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.persistence.XmlPersistence;

import java.io.File;
import java.io.IOException;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class MainActivity extends AppCompatActivity {
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp";

    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onAdded(long id) {
            MFLog.d("download id:" + id + "#onAdded");
        }

        @Override
        public void onStart(long id) {
            MFLog.d("download id:" + id + "#onStart");
        }

        @Override
        public void onComplete(long id) {
            MFLog.d("download id:" + id + "#onComplete");
        }

        @Override
        public void onFailed(long id) {
            MFLog.d("download id:" + id + "#onFailed");
        }

        @Override
        public void onCancelled(long id) {
            MFLog.d("download id:" + id + "#onCancelled");
        }

        @Override
        public void onPaused(long id) {
            MFLog.d("download id:" + id + "#onPaused");
        }

        @Override
        public void onProgressUpdate(long id, long current, long total, long bytesPerSecond) {
            MFLog.d("id:" + id + " onProgressUpdate" + " current" + StringUtil.displayFilesize(current) +
                    " total" + StringUtil.displayFilesize(total) + " %" + String.format("%.2f", (current + .0f) * 100 / total)
                    + " speed:" + StringUtil.displayFilesize(bytesPerSecond));
        }
    };
    //    Download download1 = new Download(SampleUri3, TargetDir + File.separator + "novel1.zip");
//    Download download2 = new Download(SampleUri4, TargetDir + File.separator + "novel2.zip");
//    Download download3 = new Download(SampleUri5, TargetDir + File.separator + "novel3.zip");
//    Download download4 = new Download(SampleUri6, TargetDir + File.separator + "novel4.zip", listener);
//    Download download5 = new Download(new DownloadParams(SampleUris.SampleUri2, TargetDir + File.separator + "qq.apk").
//            downloadingListener(listener));
    Download download8 = new Download(new DownloadParams(SampleUris.SampleUri8, TargetDir));

    DownloadListFragment fragment;

    DownloadObserver downloadObserver = new DownloadObserver() {
        @Override
        public void onChanged(Download download) {
            MFLog.d("onChanged id:" + download.getId() + "#status:" +
                    download.getStatus() + "#current:" + download.getCurrentBytes() + "#total:" +
                    download.getTotalBytes());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState == null) {
                fragment = DownloadListFragment.newInstance("", "");
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                        fragment, DownloadListFragment.class.getSimpleName()).commit();
            }
        }

        final Button pauseBtn = (Button) findViewById(R.id.pause_btn);
        final Button retryBtn = (Button) findViewById(R.id.retry_btn);
        final Button resumeBtn = (Button) findViewById(R.id.resume_btn);
        final Button cancelBtn = (Button) findViewById(R.id.cancel_btn);

        File dir = StorageUtils.getFilesDirectory(getApplicationContext(), true);
        try {
            XmlPersistence.getInstance().init(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Configuration configuration = new Configuration.Builder().
                downloadOperator(new DefaultDownloadOperator()).
                maxDownloadNum(2).persistence(XmlPersistence.getInstance()).debuggable(true)
                .autoStartPending(true)
                .build();
        DownloaderManager.getInstance().init(configuration);

        pauseBtn.setOnClickListener(callback);
        retryBtn.setOnClickListener(callback);
        resumeBtn.setOnClickListener(callback);
        cancelBtn.setOnClickListener(callback);

//        Download download = new Download(SampleUri2, TargetDir + File.separator + "qq.apk");
//        Download download = new Download(SampleUri1, TargetDir + File.separator + "qq.exe");

//        downloaderManager.enqueue(download1);
//        downloaderManager.enqueue(download2);
//        downloaderManager.enqueue(download3);

        DownloaderManager.getInstance().registerObserver(downloadObserver);
        File newFile = getDir("newFile", MODE_PRIVATE);
        MFLog.d("newFile=" + newFile);
        File db = getDatabasePath("new.db");
        MFLog.d("db=" + db + " " + db.exists());
    }

    private long d_id = -1;
    private OnClickEventCallback callback = new OnClickEventCallback();

    class OnClickEventCallback implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pause_btn:
                    for (int i = 0; i < 1; ++i)
                        fragment.startNextDownload();
//                    DownloaderManager.getInstance().pause(d_id);
                    break;
                case R.id.retry_btn:
//                    d_id = DownloaderManager.getInstance().enqueue(download8);
                    break;
                case R.id.resume_btn:
//                    DownloaderManager.getInstance().resume(d_id);

                    break;
                case R.id.cancel_btn:
//                    DownloaderManager.getInstance().cancel(d_id);
                    break;
            }
        }
    }
}
