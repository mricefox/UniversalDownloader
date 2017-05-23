package com.mricefox.universaldownloader;

import android.net.Uri;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/5/19
 */

public interface DownloaderConnection {
    String getFileName(Uri uri);

}
