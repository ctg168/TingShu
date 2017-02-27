package com.terry.tingshu.helpers;

import android.os.Environment;

import com.terry.tingshu.entity.Song;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2017/1/11.
 */

public class StorageHelper {








    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static class MP3Filter implements FilenameFilter {
        private MP3Filter() {
        }

        public boolean accept(File var1, String var2) {
            int var3 = var2.length() - 4;
            return var3 > 0 && (var2.startsWith(".mp3", var3) || var2.startsWith(".MP3", var3));
        }
    }
}
