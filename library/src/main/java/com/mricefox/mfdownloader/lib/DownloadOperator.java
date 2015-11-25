package com.mricefox.mfdownloader.lib;

import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/24
 */
interface DownloadOperator {
    long getRemoteFileLength(String urlStr);

    List<Block> split2Block(long len);

}
