package com.real.doctor.realdoc.util;

import java.io.File;
import java.io.FilenameFilter;

public class DataFilterUtil implements FilenameFilter {

    String extension;
    public DataFilterUtil(String extension){
        this.extension = extension;
    }

    public boolean accept(File directory, String filname) {
        // TODO Auto-generated method stub
        return filname.endsWith(extension);
    }

}
