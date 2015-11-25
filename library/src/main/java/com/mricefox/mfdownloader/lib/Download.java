package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    private final String urlStr;
    private final DownloadOperator downloadOperator;

    public Download(String urlStr, DownloadOperator downloadOperator) {
        this.urlStr = urlStr;
        this.downloadOperator = downloadOperator;
    }

    private DownloadConsumer[] splitDownload() {
        final long fileLength = downloadOperator.getRemoteFileLength(urlStr);
        List<Block> blocks = downloadOperator.split2Block(fileLength);
        return null;
    }


}
