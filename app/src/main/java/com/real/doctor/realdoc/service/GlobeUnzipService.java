package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.real.doctor.realdoc.activity.GlobeUnzipActivity;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.rxjavaretrofit.down.DownCallBack;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GlobeUnzipService extends JobService {

    public static final String TAG = GlobeUnzipService.class.getSimpleName();
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private String srcUrl;
    private String mobile;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (EmptyUtils.isNotEmpty(srcUrl)) {
                //下载zip
                downLoadData();
            }
            return true;
        }
    });

    private void downLoadData() {
        if (NetworkUtil.isNetworkAvailable(GlobeUnzipService.this)) {
            String dirPath = SDCardUtils.getGlobalDir() + srcUrl.substring(srcUrl.lastIndexOf("/") + 1, srcUrl.length());
            HttpRequestClient.getInstance(GlobeUnzipService.this).createBaseApi().download(srcUrl, dirPath, new DownCallBack() {
                @Override
                public void onProgress(long fileSizeDownloaded) {
                    super.onProgress(fileSizeDownloaded);
                    //显示下载进度
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(GlobeUnzipService.this, "文件下载失败,请返回列表重新进入病历列表!");
                }

                @Override
                public void onSucess(String filePath, String fileName, long fileSize) {
                    //zip包解压
                    try {
                        boolean zipFile = ZipUtils.unzipFile(filePath, SDCardUtils.getGlobalDir());
                        //插入数据库
                        if (zipFile) {
                            StringBuffer sb = new StringBuffer();
                            sb.append(SDCardUtils.getGlobalDir());
                            sb.append("globe");
                            sb.append(mobile);
                            String folderName = sb.toString();
                            sb.append(File.separator);
                            sb.append("datebases");
                            sb.append(File.separator);
                            sb.append(mobile);
                            sb.append(".db");
                            String path = sb.toString();
                            //将外部数据库导入内部,并删除内部数据库
                            List<SaveDocBean> list = instance.queryGlobeSaveDocList(GlobeUnzipService.this, mobile, folderName);
                            instance.insertSaveDoc(GlobeUnzipService.this, list);
                            List<ImageBean> beanList = imageInstance.queryGlobeImage(GlobeUnzipService.this, mobile, folderName);
                            imageInstance.insertImageList(GlobeUnzipService.this, beanList);
                            List<ImageListBean> imageList = imageRecycleInstance.queryGlobeImageList(GlobeUnzipService.this, mobile, folderName);
                            imageRecycleInstance.insertImageListList(GlobeUnzipService.this, imageList);
                            List<RecordBean> recordList = recordInstance.queryGlobeRecord(GlobeUnzipService.this, mobile, folderName);
                            recordInstance.insertRecordList(GlobeUnzipService.this, recordList);
                            List<VideoBean> videoList = videoInstance.queryGlobeVideo(GlobeUnzipService.this, mobile, folderName);
                            videoInstance.insertVideoList(GlobeUnzipService.this, videoList);
                            //删除压缩包
                            FileUtils.deleteDir(SDCardUtils.getGlobalDir() + "globe" + mobile + ".zip");
                        } else {
                            ToastUtil.showLong(GlobeUnzipService.this, "解压文件失败,请返回列表重新进入病历列表!");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //通知页面刷新数据(两处页面刷新,精度条finish)
                    Intent intent = new Intent(GlobeUnzipActivity.IS_UNZIP);
                    LocalBroadcastManager.getInstance(GlobeUnzipService.this).sendBroadcast(intent);
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mobile = (String) SPUtils.get(GlobeUnzipService.this, "mobile", "");
        instance = SaveDocManager.getInstance(GlobeUnzipService.this);
        imageInstance = ImageManager.getInstance(GlobeUnzipService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(GlobeUnzipService.this);
        recordInstance = RecordManager.getInstance(GlobeUnzipService.this);
        videoInstance = VideoManager.getInstance(GlobeUnzipService.this);
        if (intent != null && intent.getExtras() != null) {
            srcUrl = intent.getExtras().getString("url");
        }
        Message m = Message.obtain();
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
