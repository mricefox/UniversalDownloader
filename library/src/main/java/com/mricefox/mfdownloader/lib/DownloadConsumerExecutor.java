package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private final ThreadGroup threadGroup;

    public DownloadConsumerExecutor() {
        threadGroup = new ThreadGroup("DownlaodConsumers");

    }
}
