package com.mricefox.mfdownloader.lib;

import com.mricefox.mfdownloader.lib.operator.DownloadOperator;
import com.mricefox.mfdownloader.lib.persistence.Persistence;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/23
 */
public class Configuration {
    public static final int DEFAULT_MAX_DOWNLOAD_NUM = 5;
    private int maxDownloadNum = DEFAULT_MAX_DOWNLOAD_NUM;
    private DownloadOperator operator;
    private boolean debuggable;
    private Persistence persistence;

    public int getMaxDownloadNum() {
        return maxDownloadNum;
    }

    public DownloadOperator getDownloadOperator() {
        return operator;
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public Persistence getPersistence() {
        return persistence;
    }

    public final static class Builder {
        private Configuration configuration;

        public Builder() {
            configuration = new Configuration();
        }

        public Builder maxDownloadNum(int num) {
            configuration.maxDownloadNum = num;
            return this;
        }

        public Builder downloadOperator(DownloadOperator operator) {
            configuration.operator = operator;
            return this;
        }

        public Builder debuggable(boolean debuggable) {
            configuration.debuggable = debuggable;
            return this;
        }

        public Builder persistence(Persistence persistence) {
            configuration.persistence = persistence;
            return this;
        }

        public Configuration build() {
            return configuration;
        }
    }
}
