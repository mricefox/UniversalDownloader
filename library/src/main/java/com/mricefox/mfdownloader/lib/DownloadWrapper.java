package com.mricefox.mfdownloader.lib;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/30
 */
public class DownloadWrapper {
    Download download;
    long id;
    List<Block> blocks;
    AtomicLong totalBytes = new AtomicLong();//muti thread call from blocks download
    AtomicLong currentBytes = new AtomicLong();//muti thread call from blocks download
    AtomicInteger status = new AtomicInteger(-1);
}
