package com.mricefox.mfdownloader.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    protected static final int DEFAULT_BLOCK_NUM = 1 << 1;

//    @Override
//    public long getRemoteFileLength(String urlStr) {
//        HttpURLConnection connection = null;
//        try {
//            URL url = new URL(urlStr);
//            connection = (HttpURLConnection) url.openConnection();
//            return connection.getContentLength();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (connection != null)
//                connection.disconnect();
//        }
//        return -1;
//    }

    @Override
    public List<Block> split2Block(long len) throws IllegalArgumentException {
        if (len < 0) {
            throw new IllegalArgumentException("Illegal Length");
        }
        final int block_num = DEFAULT_BLOCK_NUM;// TODO: 2015/11/24 by zengzifeng block num

        /**
         * e.g. split 15 bytes file into 4 block, each block size is 4, 4, 4, 3 bytes
         * each block start position and end position is 1-4, 5-8, 9-12, 13-15
         */
        List<Block> blocks = new ArrayList<>(block_num);
        long size = len / block_num;
        int extra = (int) (len % block_num);
        long offset = 0;

        for (int i = 0; i < block_num; ++i) {
            Block b = new Block();
            b.startPos = offset + 1;
            long b_size = size + (extra-- <= 0 ? 0 : 1);
            offset = b.endPos = b.startPos + b_size - 1;
            blocks.add(b);
        }
        return blocks;
    }

    @Override
    public void downloadBlock(Block block, InputStream is, File targetFile, CopyListener listener, int bufferSize) throws IOException {
        final byte[] bytes = new byte[bufferSize];
        int count = 0;
        int current = 0;
        RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
        raf.seek(block.startPos - 1);
        long skip = is.skip(block.startPos - 1);

        L.d("actually skip:" + skip + " skip:" + (block.startPos - 1));

        long size = block.endPos - block.startPos + 1;
        long remain = 0;

        while (current < size) {
            remain = size - current;
            bufferSize = remain < bufferSize ? (int) remain : bufferSize;
            if (remain == 0) break;
            count = is.read(bytes, 0, bufferSize);
            if (current + count > size) break;
//            L.d("current:"+current+"count:"+count);
            L.d("block s:" + block.startPos + " e:" + block.endPos + " buf size:" + bufferSize + " current:" + current + " count:" + count);
            raf.write(bytes, 0, count);
            current += count;
//            L.d("block s:" + block.startPos + " e:" + block.endPos + "buf size:" + bufferSize + " current:" + current + " count:" + count);
        }


//        while ((count = is.read(bytes, 0, bufferSize)) != -1
//                && current < block.endPos - block.startPos + 1) {
//            raf.write(bytes, 0, count);
//            current += count;
//            L.d("block s:" + block.startPos + " e:" + block.endPos + " current:" + current + " count:" + count);
//        }
        //should not close InputStream until all downlaod thread finish
        raf.close();
    }


    @Override
    public ContentLengthInputStream openStream(String urlStr) throws IOException {
        HttpURLConnection connection;
        InputStream stream = null;

        connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);

        if (connection.getResponseCode() == 200)//todo redirectCount
            stream = connection.getInputStream();
        else
            throw new IOException("open stream fail with response code " + connection.getResponseCode());
        return new ContentLengthInputStream(new BufferedInputStream(stream), connection.getContentLength());
    }

    /**
     * create temp file of download
     *
     * @param fileLength
     * @param fileUri
     * @throws IOException
     */
    @Override
    public void createFile(long fileLength, String fileUri) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileUri, "rw");
        raf.setLength(fileLength);
        raf.close();
    }

}
