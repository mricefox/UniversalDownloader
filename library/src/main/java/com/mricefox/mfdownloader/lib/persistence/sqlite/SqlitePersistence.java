package com.mricefox.mfdownloader.lib.persistence.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

import com.mricefox.mfdownloader.lib.Block;
import com.mricefox.mfdownloader.lib.Download;
import com.mricefox.mfdownloader.lib.DownloadParams;
import com.mricefox.mfdownloader.lib.persistence.Persistence;

import java.io.File;
import java.util.ArrayList;
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
    private SqlHelper sqlHelper;

    public SqlitePersistence(Context context, boolean testMode) {
        dbOpenHelper = new DbOpenHelper(context, testMode ? TargetDir + DB_NAME : DB_NAME);
        db = dbOpenHelper.getWritableDatabase();
        sqlHelper = new SqlHelper(db);
    }

    @Override
    public List<Download> queryAll() {
        Cursor cursor = db.rawQuery(SqlHelper.QUERY_ALL_DOWNLOAD_SQL, null);
        List<Download> downloads = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Download download = createDownloadFromCursor(cursor);
                List<Block> blocks = queryBlockByDownloadId(download.getId());
                download.setBlocks(blocks);
                downloads.add(download);
            }
        } finally {
            cursor.close();
        }
        return downloads;
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

    private List<Block> queryBlockByDownloadId(long id) {
        Cursor cursor = db.rawQuery(SqlHelper.QUERY_BLOCK_BY_DOWNLOAD_ID_SQL, new String[]{String.valueOf(id)});
        List<Block> blocks = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Block block = createBlockFromCursor(cursor);
                blocks.add(block);
            }
        } finally {
            cursor.close();
        }
        return blocks;
    }

    private Block createBlockFromCursor(Cursor cursor) {
        Block block = new Block(cursor.getInt(DbOpenHelper.BLOCK_INDEX_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.START_POSITION_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.END_POSITION_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.CURRENT_POSITION_COLUMN.columnIndex));
        return block;
    }

    private Download createDownloadFromCursor(Cursor cursor) {
        DownloadParams params = new DownloadParams(cursor.getString(DbOpenHelper.URI_COLUMN.columnIndex),
                cursor.getString(DbOpenHelper.DIR_COLUMN.columnIndex))
                .fileName(cursor.getString(DbOpenHelper.NAME_COLUMN.columnIndex))
                .priority(cursor.getInt(DbOpenHelper.PRIORITY_COLUMN.columnIndex));
        Download download = new Download(params);
        download.setId(cursor.getLong(DbOpenHelper.DOWNLOAD_ID_COLUMN.columnIndex));
        download.setTotalBytes(cursor.getLong(DbOpenHelper.TOTAL_BYTES_COLUMN.columnIndex));
        download.setCurrentBytes(cursor.getLong(DbOpenHelper.CURRENT_BYTES_COLUMN.columnIndex));
        download.setStatus(cursor.getInt(DbOpenHelper.STATUS_COLUMN.columnIndex));
        download.setElapseTimeMills(cursor.getLong(DbOpenHelper.ELAPSE_TIME_COLUMN.columnIndex));
        return download;
    }

    private void bindDownloadValues(SQLiteStatement stmt, Download download) {
        if (download.getId() > -1) {
            stmt.bindLong(DbOpenHelper.DOWNLOAD_ID_COLUMN.columnIndex + 1, download.getId());
        }
    }
}
