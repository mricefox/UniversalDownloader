package com.mricefox.mfdownloader.sample;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mricefox.mfdownloader.lib.Configuration;
import com.mricefox.mfdownloader.lib.DefaultDownloadOperator;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloaderManager;

import java.io.File;
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
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tmp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final BaseDownloadOperator imp = new BaseDownloadOperator();

//                final long len = imp.getRemoteFileLength("http://dldir1.qq.com/qqfile/qq/QQ7.8/16379/QQ7.8.exe");

//                new Handler(getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        textView.setText("len = " + len);
//                        List<Block> blocks = imp.split2Block(45678);
//                        for (int i = 0; i < blocks.size(); ++i) {
//                            Log.d("zzf", "blocks" + i + "s=" + blocks.get(i).startPos + "e=" + blocks.get(i).endPos);
//                        }
//                    }
//                }, 200);
//            }
//        }).start();

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
        DownloaderManager downloaderManager = new DownloaderManager(configuration);

//        Download download = new Download(SampleUri3, TargetDir + File.separator + "novel.zip");
        Download download = new Download(SampleUri3, TargetDir + File.separator + "qq.apk");

        downloaderManager.enqueue(download);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
