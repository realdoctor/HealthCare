package com.real.doctor.realdoc.util;

import java.io.File;
import java.io.FilenameFilter;

public class DataFilterUtil implements FilenameFilter {

    String extension;

    public DataFilterUtil(String extension) {
        this.extension = extension;
    }

    public boolean accept(File directory, String filname) {
        // TODO Auto-generated method stub
        return filname.endsWith(extension);
    }

    public static String getPath(String path) {
        //路径里只有一个文件夹
        File[] files = getFiles(path);
        String dirPath = null;
        for (int i = 0; i < files.length; i++) {
            dirPath = files[i].getAbsolutePath();
        }
        return dirPath;
    }

    public static File[] getFiles(String path) {
        File file = new File(path);
        File[] files = file.listFiles();
        return files;
    }
}
