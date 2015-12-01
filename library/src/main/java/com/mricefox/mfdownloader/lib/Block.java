package com.mricefox.mfdownloader.lib;

/**
 * Created by Bourne on 15/11/25.
 */
public class Block {
    public int index;
    public long startPos, endPos;
    public long downloadedBytes;
    transient boolean stop = false;
}
