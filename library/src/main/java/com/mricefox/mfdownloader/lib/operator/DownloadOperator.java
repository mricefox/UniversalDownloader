package com.mricefox.mfdownloader.lib.operator;

import com.mricefox.mfdownloader.lib.Block;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
public interface DownloadOperator {
    long getRemoteFileLength(String urlStr);

    List<Block> split2Block(long len) throws IllegalArgumentException;

    void downloadBlock(long downloadId, Block block, String urlStr, File targetFile, BlockDownloadListener listener, int bufferSize);

//    InputStream openStream(String urlStr) throws IOException;

    /**
     * create temp file of download
     *
     * @param fileLength
     * @param fileUri
     * @throws IOException
     */
    boolean createFile(long fileLength, String fileUri) throws IOException;
}
