package com.mricefox.mfdownloader.lib;

/**
 * Created by Bourne on 15/11/25.
 */
public class Block {
    private int index;
    private long startPos, endPos;
    private long downloadedBytes;
    private transient boolean stop = false;

    public Block(int index, long startPos, long endPos, long downloadedBytes, boolean stop) {
        this.index = index;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloadedBytes = downloadedBytes;
        this.stop = stop;
    }

    public Block(int index, long startPos, long endPos, long downloadedBytes) {
        this.index = index;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloadedBytes = downloadedBytes;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getStartPos() {
        return startPos;
    }

    public void setStartPos(long startPos) {
        this.startPos = startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public void setEndPos(long endPos) {
        this.endPos = endPos;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public long getSize() {
        return endPos - startPos + 1;
    }

    public boolean isComplete() {
        return downloadedBytes == getSize();
    }
}
