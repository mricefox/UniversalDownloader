package com.mricefox.mfdownloader.lib;

import java.io.IOException;
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
public abstract class DefaultDownloadOperator implements DownloadOperator {
    @Override
    public long getRemoteFileLength(String urlStr) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            return connection.getContentLength();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return -1;
    }

    @Override
    public List<Block> split2Block(long len) {
        final int block_num = 1 << 3;// TODO: 2015/11/24 by zengzifeng block num

        List<Block> blocks = new ArrayList<>(block_num);
        long size = len / block_num;
        int extra = (int) (len % block_num);
        long offset = -1;

        for (int i = 0; i < block_num; ++i) {
            Block b = new Block();
            b.startPos = offset + 1;
            long b_size = size + (extra-- <= 0 ? 0 : 1);
            offset = b.endPos = b.startPos + b_size - 1;
            blocks.add(b);
        }
        return blocks;
    }
}
