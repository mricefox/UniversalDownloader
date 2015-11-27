package com.mricefox.mfdownloader.lib;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
class DownloadConsumerExecutor {
    private final ThreadGroup threadGroup;
    private Executor downloadExecutor;

    public DownloadConsumerExecutor() {
        threadGroup = new ThreadGroup("downlaod-consumers");
//        downloadExecutor = Executors.newSingleThreadExecutor(threadGroup);
// TODO: 15/11/26
    }

    
}
