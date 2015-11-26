package com.mricefox.mfdownloader.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.text);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                final BaseDownloadOperator imp = new BaseDownloadOperator();

//                final long len = imp.getRemoteFileLength("http://dldir1.qq.com/qqfile/qq/QQ7.8/16379/QQ7.8.exe");

                new Handler(getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        textView.setText("len = " + len);
//                        List<Block> blocks = imp.split2Block(45678);
//                        for (int i = 0; i < blocks.size(); ++i) {
//                            Log.d("zzf", "blocks" + i + "s=" + blocks.get(i).startPos + "e=" + blocks.get(i).endPos);
//                        }
                    }
                }, 200);
            }
        }).start();


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
