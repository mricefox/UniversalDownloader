package com.mf.bourne.mfdownloader;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Configuration {
    public static final int DEFAULT_MAX_DOWNLOAD_NUM = 5;
    private int maxDownloadNum = DEFAULT_MAX_DOWNLOAD_NUM;

    public final static class Builder {
        private Configuration configuration;

        public Builder() {
            configuration = new Configuration();
        }

        public Builder maxDownloadNum(int num) {
            configuration.maxDownloadNum = num;
            return this;
        }

        public Configuration build() {
            return configuration;
        }
    }
}
