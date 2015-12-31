package com.mricefox.mfdownloader.lib;

/**
 * Created by Bourne on 15/11/25.
 */
public class Block {
    private long id = -1;//persistence
    private long downloadId;//persistence foreign key to download id
    private int index;//persistence
    private long startPos, endPos;//persistence
    private long downloadedBytes;//persistence

    public Block(int index, long startPos, long endPos, long downloadedBytes) {
        this.index = index;
        this.startPos = startPos;
        this.endPos = endPos;
        this.downloadedBytes = downloadedBytes;
    }

    public Block(long id, long downloadId, int index, long startPos, long endPos, long downloadedBytes) {
        this.id = id;
        this.downloadId = downloadId;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    public long getSize() {
        return endPos - startPos + 1;
    }

    public boolean isComplete() {
        return downloadedBytes == getSize();
    }
}
