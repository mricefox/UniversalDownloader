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
    private final static String TestDir
            = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "temp" + File.separator;
    private static final String DB_NAME = "downloads.db";

    private SQLiteDatabase db;
    private DbOpenHelper dbOpenHelper;
    private SqlHelper sqlHelper;

    public SqlitePersistence(Context context, boolean testMode) {
        dbOpenHelper = new DbOpenHelper(context, testMode ? TestDir + DB_NAME : DB_NAME);
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
    public long insert(Download download) {
        SQLiteStatement downloadStmt = sqlHelper.getInsertDownloadStatement();

        downloadStmt.clearBindings();
        bindDownloadValues(downloadStmt, download);

        long d_id = downloadStmt.executeInsert();
        download.setId(d_id);

        List<Block> blocks = download.getBlocks();
        if (blocks != null) {
            for (int i = 0, size = blocks.size(); i < size; ++i) {
                Block block = blocks.get(i);
                SQLiteStatement blockStmt = sqlHelper.getInsertBlockStatement();
                blockStmt.clearBindings();
                bindBlockValues(blockStmt, block);

                long b_id = blockStmt.executeInsert();
                block.setId(b_id);
                block.setDownloadId(d_id);
            }
        }

        return d_id;
    }

    @Override
    public long update(Download download) {
        SQLiteStatement updateDownloadStmt = sqlHelper.getUpdateDownloadStatement();

        updateDownloadStmt.clearBindings();
        bindDownloadValues(updateDownloadStmt, download);
        updateDownloadStmt.bindLong(DbOpenHelper.DOWNLOAD_TABLE_COLUMN_NUM + 1, download.getId());

        updateDownloadStmt.execute();

        List<Block> blocks = download.getBlocks();
        if (blocks != null) {
            for (int i = 0, size = blocks.size(); i < size; ++i) {
                Block block = blocks.get(i);
                SQLiteStatement blockStmt = sqlHelper.getUpdateBlockStatement();
                blockStmt.clearBindings();
                blockStmt.bindLong(DbOpenHelper.BLOCK_TABLE_COLUMN_NUM + 1, download.getId());
                bindBlockValues(blockStmt, block);

                blockStmt.execute();
            }
        }
        return 0;
    }

    @Override
    public long delete(Download download) {
        SQLiteStatement deleteDownloadStmt = sqlHelper.getDeleteDownloadStatement();
        deleteDownloadStmt.clearBindings();
        deleteDownloadStmt.bindLong(1, download.getId());

        deleteDownloadStmt.execute();

        List<Block> blocks = download.getBlocks();
        if (blocks != null) {
            for (int i = 0, size = blocks.size(); i < size; ++i) {
//                Block block = blocks.get(i);
                SQLiteStatement deleteBlockStmt = sqlHelper.getDeleteBlockStatement();
                deleteBlockStmt.clearBindings();
                deleteBlockStmt.bindLong(1, download.getId());

                deleteBlockStmt.execute();
            }
        }
        return 0;
    }

    @Override
    public Download query(long id) {
        Cursor cursor = db.rawQuery(SqlHelper.QUERY_DOWNLOAD_BY_ID_SQL, new String[]{String.valueOf(id)});
        Download download = null;

        try {
            if (cursor.moveToFirst()) {
                download = createDownloadFromCursor(cursor);

                List<Block> blocks = queryBlockByDownloadId(download.getId());
                download.setBlocks(blocks);
            }
        } finally {
            cursor.close();
        }
        return download;
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
        Block block = new Block(cursor.getLong(DbOpenHelper.BLOCK_ID_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.BLOCK_DOWNLOAD_ID_COLUMN.columnIndex),
                cursor.getInt(DbOpenHelper.BLOCK_INDEX_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.START_POSITION_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.END_POSITION_COLUMN.columnIndex),
                cursor.getLong(DbOpenHelper.DOWNLOADED_BYTES_COLUMN.columnIndex));
        return block;
    }

    private Download createDownloadFromCursor(Cursor cursor) {
        DownloadParams params = new DownloadParams(cursor.getString(DbOpenHelper.URI_COLUMN.columnIndex),
                cursor.getString(DbOpenHelper.DIR_COLUMN.columnIndex))
                .fileName(cursor.getString(DbOpenHelper.FILE_NAME_COLUMN.columnIndex))
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
        stmt.bindLong(DbOpenHelper.TOTAL_BYTES_COLUMN.columnIndex + 1, download.getTotalBytes());
        stmt.bindLong(DbOpenHelper.CURRENT_BYTES_COLUMN.columnIndex + 1, download.getCurrentBytes());
        stmt.bindLong(DbOpenHelper.STATUS_COLUMN.columnIndex + 1, download.getStatus());
        stmt.bindString(DbOpenHelper.URI_COLUMN.columnIndex + 1, download.getUri());
        stmt.bindString(DbOpenHelper.DIR_COLUMN.columnIndex + 1, download.getTargetDir());
        stmt.bindLong(DbOpenHelper.PRIORITY_COLUMN.columnIndex + 1, download.getPriority());
        stmt.bindLong(DbOpenHelper.ELAPSE_TIME_COLUMN.columnIndex + 1, download.getElapseTimeMills());
        stmt.bindString(DbOpenHelper.FILE_NAME_COLUMN.columnIndex + 1, download.getFileName());
    }

    private void bindBlockValues(SQLiteStatement stmt, Block block) {
        if (block.getId() > -1) {
            stmt.bindLong(DbOpenHelper.BLOCK_ID_COLUMN.columnIndex + 1, block.getId());
        }
        stmt.bindLong(DbOpenHelper.BLOCK_INDEX_COLUMN.columnIndex + 1, block.getIndex());
        stmt.bindLong(DbOpenHelper.START_POSITION_COLUMN.columnIndex + 1, block.getStartPos());
        stmt.bindLong(DbOpenHelper.END_POSITION_COLUMN.columnIndex + 1, block.getEndPos());
        stmt.bindLong(DbOpenHelper.DOWNLOADED_BYTES_COLUMN.columnIndex + 1, block.getDownloadedBytes());
        stmt.bindLong(DbOpenHelper.BLOCK_DOWNLOAD_ID_COLUMN.columnIndex + 1, block.getDownloadId());
    }

}
