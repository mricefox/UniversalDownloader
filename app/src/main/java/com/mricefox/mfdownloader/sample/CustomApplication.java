package com.mricefox.mfdownloader.sample;

import android.app.Application;

import com.mricefox.mfdownloader.lib.persistence.sqlite.SqlitePersistence;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/29
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SqlitePersistence sqlitePersistence = new SqlitePersistence(getApplicationContext(), true);
    }
}
