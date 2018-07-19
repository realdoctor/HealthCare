package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.real.doctor.realdoc.activity.ProgressBarActivity;
import com.real.doctor.realdoc.activity.RecordUploadActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
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
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RecordUploadService extends JobService {

    public static final String TAG = RecordUploadService.class.getSimpleName();
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private String mobile;
    private File file;
    private boolean zip = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //新建doctor文件夹
            String folderName = SDCardUtils.getGlobalDir() + "globe" + mobile + File.separator;
            boolean isFolderName = FileUtils.createOrExistsDir(folderName);
            if (!isFolderName) {
                return false;
            }
            //创造datebase中的数据库，然后复制db文件
            //将数据库导出到文件夹中,并且覆盖文件夹中的数据库
            List<SaveDocBean> list = instance.querySaveDocList(RecordUploadService.this);
            instance.insertGlobeSaveDoc(RecordUploadService.this, list, mobile, folderName);
            List<ImageBean> beanList = imageInstance.queryImageList(RecordUploadService.this);
            imageInstance.insertGlobeImageList(RecordUploadService.this, beanList, mobile, folderName);
            List<ImageListBean> imageList = imageRecycleInstance.queryImageListList(RecordUploadService.this);
            imageRecycleInstance.insertGlobelImageListList(RecordUploadService.this, imageList, mobile, folderName);
            List<RecordBean> recordList = recordInstance.queryRecordList(RecordUploadService.this);
            recordInstance.insertGlobeRecordList(RecordUploadService.this, recordList, mobile, folderName);
            List<VideoBean> videoList = videoInstance.queryVideoList(RecordUploadService.this);
            videoInstance.insertGlobeVideoList(RecordUploadService.this, videoList, mobile, folderName);
            //复制文件夹中文件到指定文件夹中
            //音频数据
            boolean isMusic = FileUtils.createOrExistsDir(folderName + "music");
            if (isMusic) {
                for (int i = 0; i < recordList.size(); i++) {
                    FileUtils.copyFile(recordList.get(i).getFilePath(), folderName + "music" + File.separator + recordList.get(i).getFileName());
                }
            }
            //视频数据
            boolean isVideo = FileUtils.createOrExistsDir(folderName + "movie");
            if (isVideo) {
                for (int i = 0; i < videoList.size(); i++) {
                    FileUtils.copyFile(videoList.get(i).getFilePath(), folderName + "movie" + File.separator + videoList.get(i).getFileName());
                }
            }
            //图片数据
            boolean isImg = FileUtils.createOrExistsDir(folderName + "img");
            if (isImg) {
                for (int i = 0; i < beanList.size(); i++) {
                    String str = beanList.get(i).getImgUrl();
                    str = str.substring(str.lastIndexOf("/") + 1, str.length());
                    FileUtils.copyFile(beanList.get(i).getImgUrl(), folderName + "img" + File.separator + str);
                }
            }
            //打包文件
            zip = false;
            //多条病历打成包
            try {
                zip = ZipUtils.zipFile(folderName, SDCardUtils.getGlobalDir() + "globe" + mobile + ".zip", "globe");
                //删除掉原来的文件夹
                FileUtils.deleteDir(folderName);
                //上传病历
                recordUpload();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    });

    private void recordUpload() {
        if (NetworkUtil.isNetworkAvailable(RecordUploadService.this) && zip) {
            Map<String, RequestBody> maps = new HashMap<>();
            File file = new File(SDCardUtils.getGlobalDir() + "globe" + mobile + ".zip");
            if (file.exists()) {
                RequestBody requestBody = DocUtils.toRequestBodyOfImage(file);
                maps.put("attach\"; filename=\"" + file.getName() + "", requestBody);//head_img图片key
            }
            HttpRequestClient.getInstance(RecordUploadService.this).createBaseApi().uploads("upload/uploadFiles/", maps, new BaseObserver<ResponseBody>(RecordUploadService.this) {
                protected Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                protected void onHandleSuccess(ResponseBody responseBody) {
                    //上传文件成功
                    String data = null;
                    String msg = null;
                    String code = null;
                    try {
                        data = responseBody.string().toString();
                        try {
                            JSONObject object = new JSONObject(data);
                            if (DocUtils.hasValue(object, "msg")) {
                                msg = object.getString("msg");
                            }
                            if (DocUtils.hasValue(object, "code")) {
                                code = object.getString("code");
                            }
                            if (msg.equals("ok") && code.equals("0")) {
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传成功!");
                            } else {
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                            }
                            //动态注册广播,通知病历上传成功
                            Intent intent = new Intent(RecordUploadActivity.UPLOAD_RECORD);
                            LocalBroadcastManager.getInstance(RecordUploadService.this).sendBroadcast(intent);
                            //删除压缩文件
                            if (zip) {
                                FileUtils.deleteDir(SDCardUtils.getGlobalDir() + "globe" + mobile + ".zip");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (zip) {
                        //删除压缩文件
                        FileUtils.deleteDir(SDCardUtils.getGlobalDir() + "globe" + mobile + ".zip");
                    }
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                    Log.d(TAG, e.getMessage());
                    if (disposable != null && !disposable.isDisposed()) {
                        disposable.dispose();
                    }
                }

                @Override
                public void onComplete() {
                    if (disposable != null && !disposable.isDisposed()) {
                        disposable.dispose();
                    }
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = SaveDocManager.getInstance(RecordUploadService.this);
        imageInstance = ImageManager.getInstance(RecordUploadService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(RecordUploadService.this);
        recordInstance = RecordManager.getInstance(RecordUploadService.this);
        videoInstance = VideoManager.getInstance(RecordUploadService.this);
        mobile = (String) SPUtils.get(RecordUploadService.this, "mobile", "");
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
