package com.mricefox.universaldownloader;

import android.net.Uri;
import android.util.SparseArray;

import java.io.File;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public class DownloadModel {
    private int id;
    private Uri uri;
    private File targetDir;
    private String fileName;
    private SparseArray<Block> blocks;
    private long totalBytes;
    private long downloadedBytes;

    public static class Block {
        private int id;
        private long startPos, endPos;
        private long downloadedBytes;
    }
}
