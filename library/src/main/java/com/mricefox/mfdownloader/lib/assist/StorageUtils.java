package com.mricefox.mfdownloader.lib.assist;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Author:zengzifeng email:zeng163mail@163.com
 * Description:
 * Date:2015/12/30
 */
public class StorageUtils {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private StorageUtils() {
    }

    /**
     * get application files directory
     *
     * @param context
     * @param preferExternal Whether prefer external location for cache
     * @return
     */
    public static File getFilesDirectory(Context context, boolean preferExternal) {
        File filesDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) {
            externalStorageState = "";
        }
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            filesDir = getExternalFilesDir(context);
        }
        if (filesDir == null) {
            filesDir = context.getFilesDir();
        }
        if (filesDir == null) {
            String filesDirPath = "/data/data/" + context.getPackageName() + "/files/";
            MFLog.w("Can't define system cache directory! '" + filesDirPath + "' will be used.");
            filesDir = new File(filesDirPath);
        }
        return filesDir;
    }

    private static File getExternalFilesDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File filesDir = new File(new File(dataDir, context.getPackageName()), "files");
        if (!filesDir.exists()) {
            if (!filesDir.mkdirs()) {
                MFLog.w("Unable to create external cache directory");
                return null;
            }
            try {//avoid media scan
                new File(filesDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                MFLog.w("Can't create \".nomedia\" file in application external files directory");
            }
        }
        return filesDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
