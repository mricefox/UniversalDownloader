package com.mf.bourne.mfdownloader;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
public interface IDownload {
    public long getRemoteFileLength(String urlStr);

    public List<Block> split2Block(long len);

}
