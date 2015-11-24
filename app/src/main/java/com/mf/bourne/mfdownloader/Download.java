package com.mf.bourne.mfdownloader;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    private final String url;
    private final IDownload iDownload;

//    public final static class Builder {
//        public
//    }

    public Download(String url, IDownload iDownload) {
        this.url = url;
        this.iDownload = iDownload;
    }

    private SubDownload[] splitDownload() {
        List<Block> blocks = iDownload.split2Block(iDownload.getRemoteFileLength(url));
        return null;
    }


}
