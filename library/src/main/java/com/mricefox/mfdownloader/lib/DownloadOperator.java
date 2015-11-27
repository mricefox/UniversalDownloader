package com.mricefox.mfdownloader.lib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
public interface DownloadOperator {
//    long getRemoteFileLength(String urlStr);

    List<Block> split2Block(long len) throws IllegalArgumentException;

    void downloadBlock(Block block, InputStream is, File targetFile, CopyListener listener, int bufferSize) throws IOException;

    InputStream openStream(String urlStr) throws IOException;

    /**
     * create temp file of download
     *
     * @param fileLength
     * @param fileUri
     * @throws IOException
     */
    void createFile(long fileLength, String fileUri) throws IOException;
}
