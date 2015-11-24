package com.mf.bourne.mfdownloader;

import java.util.concurrent.Executors;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
public class SubDownload extends Download {
    private long startPos, endPos;


    public SubDownload(String url) {
        super(url);
    }
}
