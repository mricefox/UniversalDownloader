package com.mricefox.mfdownloader.lib.persistence.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/29
 */
public class SqlHelper {
    static String QUERY_ALL_DOWNLOAD_SQL;
    static String QUERY_BLOCK_BY_DOWNLOAD_ID_SQL;
    private SQLiteDatabase db;

    private SQLiteStatement insertDownloadStatement;

    public SqlHelper(SQLiteDatabase db) {
        this.db = db;
        QUERY_ALL_DOWNLOAD_SQL = "SELECT * FROM " + DbOpenHelper.DOWNLOAD_TABLE_NAME;
        QUERY_BLOCK_BY_DOWNLOAD_ID_SQL = "SELECT * FROM " + DbOpenHelper.BLOCK_TABLE_NAME
                + " WHERE " + DbOpenHelper.BLOCK_DOWNLOAD_ID_COLUMN + " = ?";
    }

    static class Property {
        final String columnName;
        final String type;
        final int columnIndex;
        final ForeignKey foreignKey;

        public Property(String columnName, String type, int columnIndex) {
            this(columnName, type, columnIndex, null);
        }

        public Property(String columnName, String type, int columnIndex, ForeignKey foreignKey) {
            this.columnName = columnName;
            this.type = type;
            this.columnIndex = columnIndex;
            this.foreignKey = foreignKey;
        }
    }

    static class ForeignKey {
        final String targetTable;
        final String targetFieldName;

        public ForeignKey(String targetTable, String targetFieldName) {
            this.targetTable = targetTable;
            this.targetFieldName = targetFieldName;
        }
    }

    public static String create(String tableName, Property primaryKey, Property... properties) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        builder.append(tableName).append(" (");
        builder.append(primaryKey.columnName).append(" ");
        builder.append(primaryKey.type);
        builder.append("  PRIMARY KEY AUTOINCREMENT ");
        for (Property property : properties) {
            builder.append(", ").append(property.columnName).append(" ").append(property.type);
        }
        for (Property property : properties) {
            if (property.foreignKey != null) {
                ForeignKey key = property.foreignKey;
                builder.append(", FOREIGN KEY(").append(property.columnName)
                        .append(") REFERENCES ").append(key.targetTable).append("(")
                        .append(key.targetFieldName).append(") ON DELETE CASCADE");
            }
        }
        builder.append(" );");
        return builder.toString();
    }

    public SQLiteStatement getInsertDownloadStatement() {
        if (insertDownloadStatement == null) {
            StringBuilder builder = new StringBuilder("INSERT INTO ").append(DbOpenHelper.DOWNLOAD_TABLE_NAME);
            builder.append(" VALUES (");
            for (int i = 0; i < DbOpenHelper.DOWNLOAD_TABLE_COLUMN_NUM; ++i) {
                builder.append(i > 0 ? ",?" : "?");
            }
            builder.append(")");
            insertDownloadStatement = db.compileStatement(builder.toString());
        }
        return insertDownloadStatement;
    }
}
