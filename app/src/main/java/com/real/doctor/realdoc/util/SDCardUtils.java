package com.real.doctor.realdoc.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: zhujiabin
 * time  : 2018/1/3
 * desc  : SD卡相关的辅助类
 */

public class SDCardUtils {
    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否可用
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 图片保存到SD卡
     *
     * @param bitmap
     * @return
     */
    public static String saveToSdCard(String path, Bitmap bitmap, String name) {
        String imageUrl = path + name;
        File file = new File(imageUrl);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    /**
     * 获得文章图片保存路径
     *
     * @return
     */
    public static String getGlobalDir() {
        String imageCacheUrl = getSDCardPath() + "RealDoc" + File.separator;
        File file = new File(imageCacheUrl);
        if (!file.exists())
            file.mkdir();  //如果不存在则创建
        return imageCacheUrl;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 删除文件
     **/
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists())
            file.delete(); // 删除文件
    }

    /**
     * 根据路径，获取图片，并将图片放置在指定文件夹下
     */
    public static boolean saveToSdCard(String path, String folder) {
        File file = new File(path);
        //若该文件存在
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            String imageCacheUrl = getGlobalDir() + folder;
            if (EmptyUtils.isNotEmpty(imageCacheUrl)) {
                //截图图片名称,保存到sdcard中
                String fileName = path.substring(path.lastIndexOf("/") + 1, path.length());
                //创建路径
                File folderFile = new File(imageCacheUrl + File.separator + "images");
                if (!folderFile.exists())
                    folderFile.mkdirs();
                String imageUrl = folderFile + File.separator + fileName;
                File newFile = new File(imageUrl);
                try {
                    FileOutputStream out = new FileOutputStream(newFile);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断SD卡上的文件夹是否存在
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(getSDCardPath() + fileName);
        return file.exists();
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(getSDCardPath() + dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断绝对路径下文件夹是否存在
     *
     * @param path
     * @return
     */
    public static boolean fileExit(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param filePath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteOneFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteOneFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteOneFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 在原来的路径上创建文件夹
     *
     * @param base
     * @param name
     * @return
     */
    public static File creatFileDir(String base, String name) {
        File dir = new File(base + "/" + name);
        if (dir.exists()) {
        } else {
            dir.mkdir();
        }
        return dir;
    }

}

