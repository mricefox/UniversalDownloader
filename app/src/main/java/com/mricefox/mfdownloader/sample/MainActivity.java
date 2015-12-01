package com.mricefox.mfdownloader.sample;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mricefox.mfdownloader.lib.Configuration;
import com.mricefox.mfdownloader.lib.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloaderManager;
import com.mricefox.mfdownloader.lib.DownloadingListener;
import com.mricefox.mfdownloader.lib.L;
import com.mricefox.mfdownloader.lib.XmlPersistence;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class MainActivity extends AppCompatActivity {
    private final static String SampleUri1 = "http://dldir1.qq.com/qqfile/qq/QQ7.8/16379/QQ7.8.exe";
    private final static String SampleUri2 = "http://sqdd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
    private final static String SampleUri3 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271444197407.zip";
    private final static String SampleUri4 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271438127289.zip";
    private final static String SampleUri5 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271434586180.zip";
    private final static String SampleUri6 = "http://file.txtbook.com.cn/20110730/web/down20090411/2015-11/201511271423353240.zip";
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp";

//    DownloaderManager downloaderManager;

    Download download1 = new Download(SampleUri3, TargetDir + File.separator + "novel1.zip");
    Download download2 = new Download(SampleUri4, TargetDir + File.separator + "novel2.zip");
    Download download3 = new Download(SampleUri5, TargetDir + File.separator + "novel3.zip");
    Download download4 = new Download(SampleUri6, TargetDir + File.separator + "novel4.zip", new DownloadingListener() {
        @Override
        public void onStart(long id) {
            L.d("download id:" + id + "#onStart");
        }

        @Override
        public void onComplete(long id) {
            L.d("download id:" + id + "#onComplete");
        }

        @Override
        public void onFailed(long id) {
            L.d("download id:" + id + "#onFailed");
        }

        @Override
        public void onCancelled(long id) {
            L.d("download id:" + id + "#onCancelled");
        }

        @Override
        public void onPaused(long id) {
            L.d("download id:" + id + "#onPaused");
        }

        @Override
        public void onProgressUpdate(long id, long current, long total, long bytesPerSecond) {
            L.d("download id:" + id + "#onProgressUpdate" + "#current" + current + "#total" + total);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);
        final Button pauseBtn = (Button) findViewById(R.id.pause_btn);
        final Button retryBtn = (Button) findViewById(R.id.retry_btn);

//        final AtomicInteger n = new AtomicInteger(1);

//        final Executor executor = Executors.newCachedThreadPool();
//        final Executor executor = new ThreadPoolExecutor(1,5,5000,TimeUnit.MILLISECONDS,);
//        final Executor executor = Executors.newFixedThreadPool(2);

//        for (int i = 0; i < 5; ++i)
//            executor.execute(new Task(n.getAndIncrement()));

//        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
//            @Override
//            public void run() {
//                executor.execute(new Task(n.getAndIncrement()));
//            }
//        }, 5000, TimeUnit.MILLISECONDS);

        Configuration configuration = new Configuration.Builder().
                downloadOperator(new DefaultDownloadOperator()).
                maxDownloadNum(5)
                .build();
//        downloaderManager =  DownloaderManager.getInstance();
        DownloaderManager.getInstance().init(configuration);

        pauseBtn.setOnClickListener(callback);
        retryBtn.setOnClickListener(callback);

//        Download download = new Download(SampleUri2, TargetDir + File.separator + "qq.apk");
//        Download download = new Download(SampleUri1, TargetDir + File.separator + "qq.exe");

//        downloaderManager.enqueue(download1);
//        downloaderManager.enqueue(download2);
//        downloaderManager.enqueue(download3);

    }

    private long d_id = -1;
    private OnClickEventCallback callback = new OnClickEventCallback();

    class OnClickEventCallback implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pause_btn:
//                    DownloaderManager.getInstance().pause(d_id);
                    XmlPersistence.getInstance().insert(null);
                    break;
                case R.id.retry_btn:
//                    d_id = DownloaderManager.getInstance().enqueue(download4);
                    try {
                        XmlPersistence.getInstance().init(TargetDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    private class Task implements Runnable {
        int n;

        Task(int n) {
            this.n = n;
        }

        @Override
        public void run() {
            try {
//                Log.d("zzf", "task id:" + n + " prepare" + " thread name:" + Thread.currentThread());
                Thread.sleep(100 * n);
//                Log.d("zzf", "task id:" + n + " finish" + " thread name:" + Thread.currentThread());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
