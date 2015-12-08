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

    private int maxDownloadNum;
    private DownloadOperator operator;
    private boolean debuggable;
    private Persistence persistence;
    private boolean autoStartPending;

    private Configuration(Builder builder) {
        maxDownloadNum = builder.maxDownloadNum;
        operator = builder.operator;
        debuggable = builder.debuggable;
        persistence = builder.persistence;
        autoStartPending = builder.autoStartPending;
    }

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

    public boolean isAutoStartPending() {
        return autoStartPending;
    }

    public static class Builder {
        private int maxDownloadNum = DEFAULT_MAX_DOWNLOAD_NUM;
        private DownloadOperator operator;
        private boolean debuggable;
        private Persistence persistence;
        private boolean autoStartPending;

        public Builder() {
        }

        public Builder maxDownloadNum(int num) {
            maxDownloadNum = num;
            return this;
        }

        public Builder downloadOperator(DownloadOperator operator) {
            this.operator = operator;
            return this;
        }

        public Builder debuggable(boolean debuggable) {
            this.debuggable = debuggable;
            return this;
        }

        public Builder persistence(Persistence persistence) {
            this.persistence = persistence;
            return this;
        }

        public Builder autoStartPending(boolean autoStartPending) {
            this.autoStartPending = autoStartPending;
            return this;
        }

        public Configuration build() {
            return new Configuration(this);
        }
    }
}
