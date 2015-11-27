package com.mricefox.mfdownloader.lib;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Download {
    private final String uri;
    private final String targetFilePath;

    public Download(String uri, String targetFilePath) {
        this.uri = uri;
        this.targetFilePath = targetFilePath;
    }

    public String getUri() {
        return uri;
    }

    public String getTargetFilePath() {
        return targetFilePath;
    }

}
