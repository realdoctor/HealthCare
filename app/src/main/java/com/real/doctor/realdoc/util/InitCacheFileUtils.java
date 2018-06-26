package com.real.doctor.realdoc.util;

import com.real.doctor.realdoc.util.SDCardUtils;

/**
 * 初始化文件夹
 * Created by kuyue on 2017/6/20 下午3:38.
 * 邮箱:595327086@qq.com
 */

public class InitCacheFileUtils {

    /**
     * 初始化缓存目录，一般为两级，所以不考虑三级的情况
     *
     * @param dirFileName
     * @param imgFileName
     */
    public static void initImgDir(String dirFileName, String imgFileName) {

        //创建文件夹
        if (SDCardUtils.isSDCardEnable()) {//存在sd
            if (!SDCardUtils.isFileExist(dirFileName)) {
                //创建主目录
                SDCardUtils.creatSDDir(dirFileName);
            }
            if (EmptyUtils.isEmpty(SDCardUtils.fileExit(SDCardUtils.getSDCardPath() + dirFileName + "/" + imgFileName))) {
                //创建目录
                SDCardUtils.creatFileDir(SDCardUtils.getSDCardPath() + dirFileName, imgFileName);
            }
        }
    }

}
