// IDownloaderService.aidl
package com.mricefox.universaldownloader;

// Declare any non-default types here with import statements

interface IDownloaderService {
    void start(in Uri uri, in Uri target);
}
