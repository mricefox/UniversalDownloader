package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/30
 */
public class DownloadWrapper {
    public Download download;
   public long id;
    public List<Block> blocks;
    //    AtomicLong totalBytes = new AtomicLong();//muti thread call from blocks download
//    AtomicLong currentBytes = new AtomicLong();//muti thread call from blocks download
//    AtomicInteger status = new AtomicInteger(-1);
    long totalBytes;
    long currentBytes;
    int status;

    boolean allBlockStopped() {
        for (int i = 0, size = blocks.size(); i < size; ++i)
            if (!blocks.get(i).stop) return false;
        return true;
    }
}
