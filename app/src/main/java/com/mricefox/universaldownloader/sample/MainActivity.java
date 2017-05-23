package com.mricefox.universaldownloader.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mricefox.universaldownloader.UniversalDownloader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.begin_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UniversalDownloader.inst().create(null).setCallback(null).enqueue();
            }
        });
    }


}
