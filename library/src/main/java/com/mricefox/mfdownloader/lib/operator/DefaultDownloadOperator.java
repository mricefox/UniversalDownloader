package com.mricefox.mfdownloader.lib.operator;

import com.mricefox.mfdownloader.lib.Block;
import com.mricefox.mfdownloader.lib.assist.ContentLengthInputStream;
import com.mricefox.mfdownloader.lib.assist.MFLog;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
public class DefaultDownloadOperator implements DownloadOperator {
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    /**
     * {@value}
     */
    public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

    /**
     * {@value}
     */
    public static final int BUFFER_SIZE = 32 * 1024; // 32 Kb

    /**
     * {@value}
     */
    protected static final int DEFAULT_BLOCK_NUM = 1 << 2;

    @Override
    public long getRemoteFileLength(String urlStr) {
        long time = System.currentTimeMillis();
        MFLog.d("start getRemoteFileLength");
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
            if (connection.getResponseCode() == 200)//todo redirectCount
                return connection.getContentLength();
            else
                throw new IOException("open stream fail with response code " + connection.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
            MFLog.d("end getRemoteFileLength time:" + (System.currentTimeMillis() - time));
        }
        return -1;
    }

    @Override
    public List<Block> split2Block(long len) throws IllegalArgumentException {
        if (len <= 0) {
            throw new IllegalArgumentException("Illegal Length:" + len);
        }
        final int block_num = DEFAULT_BLOCK_NUM;// TODO: 2015/11/24 by zengzifeng block num

        /**
         * e.g. split 15 bytes file into 4 block, each block size is 4, 4, 4, 3 bytes
         * each block start position and end position is 0-3, 4-7, 8-11, 12-14
         */
        List<Block> blocks = new ArrayList<>(block_num);
        long size = len / block_num;
        int extra = (int) (len % block_num);
        long offset = -1;

        for (int i = 0; i < block_num; ++i) {
            Block b = new Block(i, offset + 1, 0, 0);
            long b_size = size + (extra-- <= 0 ? 0 : 1);
            offset = b.getStartPos() + b_size - 1;
            b.setEndPos(offset);
            blocks.add(b);
        }
        return blocks;
    }

    @Override
    public void downloadBlock(long downloadId, long startPos, long endPos, int blockIndex,
                              String urlStr, File targetFile, BlockDownloadListener listener, int bufferSize) {
        HttpURLConnection connection = null;
        RandomAccessFile raf = null;
        InputStream is = null;
        int current = 0;
//        MFLog.d("downloadBlock:s#" + startPos + " e#" + endPos);
        try {
            connection = (HttpURLConnection) new URL(urlStr).openConnection();
            connection.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
            connection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);

//            if (connection.getResponseCode() == 200) {
            final byte[] bytes = new byte[bufferSize];
            int count = 0;
            raf = new RandomAccessFile(targetFile, "rw");
            raf.seek(startPos);
            is = new ContentLengthInputStream(new BufferedInputStream(connection.getInputStream()),
                    connection.getContentLength());

            while ((count = is.read(bytes, 0, bufferSize)) != -1) {
                raf.write(bytes, 0, count);
                current += count;
//                MFLog.d("block s:" + startPos + " e:" + endPos + " buf size:" + bufferSize + " current:" + current + " count:" + count);
                if (listener != null && !listener.onBytesDownload(downloadId, blockIndex, current, is.available(), count))
                    break;
            }
            if (listener != null) listener.onDownloadStop(downloadId, blockIndex, current);
//            } else {
//                throw new IOException("open stream fail with response code " + connection.getResponseCode());
//            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) listener.onDownloadFail(downloadId, blockIndex, current);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) listener.onDownloadFail(downloadId, blockIndex, current);
        } finally {
            close(raf);
            close(is);
            if (connection != null) connection.disconnect();
        }

//        long size = block.endPos - block.startPos + 1;
//        long remain = 0;

//        while (current < size) {
//            remain = size - current;
//            bufferSize = remain < bufferSize ? (int) remain : bufferSize;
//            if (remain == 0) break;
//            count = is.read(bytes, 0, bufferSize);
//            if (current + count > size) break;
//            MFLog.d("block s:" + block.startPos + " e:" + block.endPos + " buf size:" + bufferSize + " current:" + current + " count:" + count);
//            raf.write(bytes, 0, count);
//            current += count;
//        }


//        while ((count = is.read(bytes, 0, bufferSize)) != -1
//                && current < block.endPos - block.startPos + 1) {
//            raf.write(bytes, 0, count);
//            current += count;
//            MFLog.d("block s:" + block.startPos + " e:" + block.endPos + " current:" + current + " count:" + count);
//        }
        //should not close InputStream until all downlaod thread finish
//        raf.close();
    }


//    @Override
//    public ContentLengthInputStream openStream(String urlStr) throws IOException {
//        HttpURLConnection connection;
//        InputStream stream = null;
//
//        connection = (HttpURLConnection) new URL(urlStr).openConnection();
//        connection.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
//        connection.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
//
//        if (connection.getResponseCode() == 200)//todo redirectCount
//            stream = connection.getInputStream();
//        else
//            throw new IOException("open stream fail with response code " + connection.getResponseCode());
//        return new ContentLengthInputStream(new BufferedInputStream(stream), connection.getContentLength());
//    }

    /**
     * create temp file of download
     *
     * @param fileLength
     * @param fileUri
     * @throws IOException
     */
    @Override
    public boolean createFile(long fileLength, String fileUri) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileUri, "rw");
            raf.setLength(fileLength);
            raf.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void close(Closeable closeable) {
        if (closeable != null) try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
