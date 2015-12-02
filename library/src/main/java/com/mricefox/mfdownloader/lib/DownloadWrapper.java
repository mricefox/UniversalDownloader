package com.mricefox.mfdownloader.lib;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/11/30
 */
public class DownloadWrapper {
    private Download download;
    private long id;
    private List<Block> blocks = new ArrayList<>();
    private long totalBytes;
    private long currentBytes;
    private int status;

    public DownloadWrapper(Download download, long id, List<Block> blocks, long totalBytes, long currentBytes, int status) {
        this.download = download;
        this.id = id;
        this.blocks = blocks;
        this.totalBytes = totalBytes;
        this.currentBytes = currentBytes;
        this.status = status;
    }

    public DownloadWrapper(Download download, long id) {
        this.download = download;
        this.id = id;
    }

    public DownloadWrapper(Download download, long id, int status) {
        this.download = download;
        this.id = id;
        this.status = status;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(long currentBytes) {
        this.currentBytes = currentBytes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    boolean allBlockStopped() {
        for (int i = 0, size = blocks.size(); i < size; ++i)
            if (!blocks.get(i).isStop()) return false;
        return true;
    }
}
