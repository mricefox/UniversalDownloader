package com.mricefox.mfdownloader.lib.persistence.sqlite;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/29
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    static final String DOWNLOAD_TABLE_NAME = "download";
    static final String BLOCK_TABLE_NAME = "block";

    //download attributes
    static final SqlHelper.Property DOWNLOAD_ID_COLUMN = new SqlHelper.Property("_id", "LONG", 0);
    static final SqlHelper.Property TOTAL_BYTES_COLUMN = new SqlHelper.Property("total_bytes", "LONG", 1);
    static final SqlHelper.Property CURRENT_BYTES_COLUMN = new SqlHelper.Property("current_bytes", "LONG", 2);
    static final SqlHelper.Property STATUS_COLUMN = new SqlHelper.Property("status", "INTEGER", 3);
    static final SqlHelper.Property URI_COLUMN = new SqlHelper.Property("uri", "TEXT", 4);
    static final SqlHelper.Property DIR_COLUMN = new SqlHelper.Property("dir", "TEXT", 5);
    static final SqlHelper.Property PRIORITY_COLUMN = new SqlHelper.Property("priority", "INTEGER", 6);
    static final SqlHelper.Property ELAPSE_TIME_COLUMN = new SqlHelper.Property("elapse_time", "LONG", 7);
    static final SqlHelper.Property FILE_NAME_COLUMN = new SqlHelper.Property("name", "TEXT", 8);
    static final int DOWNLOAD_TABLE_COLUMN_NUM = 9;
    static final SqlHelper.Property[] DOWNLOAD_TABLE_COLUMN_ARR = {DOWNLOAD_ID_COLUMN, TOTAL_BYTES_COLUMN, CURRENT_BYTES_COLUMN,
            STATUS_COLUMN, URI_COLUMN, DIR_COLUMN, PRIORITY_COLUMN, ELAPSE_TIME_COLUMN, FILE_NAME_COLUMN};

    //block attributes
    static final SqlHelper.Property BLOCK_ID_COLUMN = new SqlHelper.Property("_id", "LONG", 0);
    static final SqlHelper.Property BLOCK_INDEX_COLUMN = new SqlHelper.Property("block_index", "INTEGER", 1);
    static final SqlHelper.Property START_POSITION_COLUMN = new SqlHelper.Property("start_position", "LONG", 2);
    static final SqlHelper.Property END_POSITION_COLUMN = new SqlHelper.Property("end_position", "LONG", 3);
    static final SqlHelper.Property DOWNLOADED_BYTES_COLUMN = new SqlHelper.Property("current_position", "LONG", 4);
    static final SqlHelper.Property BLOCK_DOWNLOAD_ID_COLUMN = new SqlHelper.Property("download_id", "LONG", 5, new SqlHelper.ForeignKey(DOWNLOAD_TABLE_NAME, DOWNLOAD_ID_COLUMN.columnName));
    static final int BLOCK_TABLE_COLUMN_NUM = 6;
    static final SqlHelper.Property[] BLOCK_TABLE_COLUMN_ARR = {BLOCK_ID_COLUMN, BLOCK_INDEX_COLUMN, START_POSITION_COLUMN, END_POSITION_COLUMN,
            DOWNLOADED_BYTES_COLUMN, BLOCK_DOWNLOAD_ID_COLUMN};

    public DbOpenHelper(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DbOpenHelper(Context context, String name, DatabaseErrorHandler errorHandler) {
        super(context, name, null, DB_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDownloadTableSql = SqlHelper.create(DOWNLOAD_TABLE_NAME, DOWNLOAD_ID_COLUMN,
                TOTAL_BYTES_COLUMN,
                CURRENT_BYTES_COLUMN,
                STATUS_COLUMN,
                URI_COLUMN,
                DIR_COLUMN,
                PRIORITY_COLUMN,
                ELAPSE_TIME_COLUMN,
                FILE_NAME_COLUMN);
        db.execSQL(createDownloadTableSql);

        String createBlockTableSql = SqlHelper.create(BLOCK_TABLE_NAME, BLOCK_ID_COLUMN,
                BLOCK_INDEX_COLUMN,
                START_POSITION_COLUMN,
                END_POSITION_COLUMN,
                DOWNLOADED_BYTES_COLUMN,
                BLOCK_DOWNLOAD_ID_COLUMN);
        db.execSQL(createBlockTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
