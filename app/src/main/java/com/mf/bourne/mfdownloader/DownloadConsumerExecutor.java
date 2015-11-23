package com.mf.bourne.mfdownloader;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class DownloadConsumerExecutor {
    private final ThreadGroup threadGroup;

    public DownloadConsumerExecutor() {
        threadGroup = new ThreadGroup("DownlaodConsumers");
    }
}
