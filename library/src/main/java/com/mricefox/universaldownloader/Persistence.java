package com.mricefox.universaldownloader;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/23
 */

public interface Persistence {
    int insert(DownloadModel model);

    DownloadModel queryById(int id);
}
