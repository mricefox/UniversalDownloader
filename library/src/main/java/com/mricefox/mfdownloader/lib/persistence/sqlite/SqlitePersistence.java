package com.mricefox.mfdownloader.lib.persistence.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.persistence.Persistence;

import java.io.File;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/29
 */
public class SqlitePersistence implements Persistence<Download> {
    private final static String TargetDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp" + File.separator;
    private static final String DB_NAME = "downloads.db";

    private SQLiteDatabase db;
    private DbOpenHelper dbOpenHelper;

    public SqlitePersistence(Context context, boolean testMode) {
        dbOpenHelper = new DbOpenHelper(context, testMode ? TargetDir + DB_NAME : DB_NAME);
        db = dbOpenHelper.getWritableDatabase();
    }

    @Override
    public List<Download> queryAll() {
        return null;
    }

    @Override
    public long insert(Download entity) {
        return 0;
    }

    @Override
    public long update(Download entity) {
        return 0;
    }

    @Override
    public long delete(Download entity) {
        return 0;
    }

    @Override
    public Download query(long id) {
        return null;
    }
}
