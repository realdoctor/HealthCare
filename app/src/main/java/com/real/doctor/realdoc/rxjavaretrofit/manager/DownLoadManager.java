package com.real.doctor.realdoc.rxjavaretrofit.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.real.doctor.realdoc.rxjavaretrofit.down.DownCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.manager
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */
public class DownLoadManager {

    private DownCallBack callBack;

    private static final String TAG = "DownLoadManager";

    private Handler handler;

    public DownLoadManager(DownCallBack callBack) {
        this.callBack = callBack;
    }

    private static DownLoadManager sInstance;

    private String name;

    /**
     * DownLoadManager getInstance
     */
    public static synchronized DownLoadManager getInstance(DownCallBack callBack) {
        if (sInstance == null) {
            sInstance = new DownLoadManager(callBack);
        }
        return sInstance;
    }


    public boolean writeResponseBodyToDisk(Context context, ResponseBody body, final String dirPath) {

        name = dirPath.substring(dirPath.lastIndexOf("/") + 1, dirPath.length());
        String destFileDir = dirPath.substring(0, dirPath.lastIndexOf("/") + 1);
        try {
            // todo change the file location/name according to your needs
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File futureStudioIconFile = new File(dir, name);
            if (futureStudioIconFile.exists()) {
                futureStudioIconFile.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                final long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                Log.d(TAG, "file length: " + fileSize);
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    if (callBack != null) {
                        handler = new Handler(Looper.getMainLooper());
                        final long finalFileSizeDownloaded = fileSizeDownloaded;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onProgress(finalFileSizeDownloaded);
                            }
                        });

                    }
                }

                outputStream.flush();
                Log.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                if (callBack != null) {
                    handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSucess(dirPath, name, fileSize);

                        }
                    });
                    Log.d(TAG, "file downloaded: " + fileSizeDownloaded + " of " + fileSize);
                }

                return true;
            } catch (IOException e) {
                if (callBack != null) {
                    callBack.onError(e);
                }
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            if (callBack != null) {
                callBack.onError(e);
            }
            return false;
        }
    }
}
