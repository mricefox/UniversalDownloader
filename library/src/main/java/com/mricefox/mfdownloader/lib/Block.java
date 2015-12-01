package com.mricefox.mfdownloader.lib;

/**
 * Created by Bourne on 15/11/25.
 */
public class Block {
    int index;
    long startPos, endPos;
    long downloadedBytes;
    boolean stop = false;
}
