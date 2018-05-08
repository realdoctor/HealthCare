package com.real.doctor.realdoc.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.real.doctor.realdoc.activity.ProgressBarActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.util.ZipUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UpdateService extends JobService {

    public static final String TAG = UpdateService.class.getSimpleName();
    private List<SaveDocBean> mList = new ArrayList<>();
    private List<Boolean> mFlag = new ArrayList<>();
    private boolean zip = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mFlag.clear();
            for (SaveDocBean bean : mList) {
                String mImg = bean.getImgs();
                if (EmptyUtils.isNotEmpty(mImg)) {
                    mFlag.add(true);
                    String[] imgs = bean.getImgs().split(";");
                    if (SDCardUtils.isSDCardEnable()) {
                        //新建doctor文件夹
                        String folderName = SDCardUtils.getSDCardPath() + "doctor" + File.separator;
                        File file = new File(folderName);
                        if (!file.exists())
                            file.mkdir();  //如果不存在则创建
                        boolean isEmptyFolder = FileUtils.deleteAllInDir(SDCardUtils.getSDCardPath() + "doctor");
                        if (isEmptyFolder) {
                            //复制图片
                            for (String img : imgs) {
                                Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/RealDoc/" + img);//filePath
                                if (EmptyUtils.isNotEmpty(bitmap)) {
                                    SDCardUtils.saveToSdCard(folderName, bitmap, img);
                                }
                            }
                            zip = false;
                            //打包
                            try {
                                zip = ZipUtils.zipFile(folderName, SDCardUtils.getSDCardPath() + "doctor.zip", "doctor");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            if (zip) {
//                                String size = FileUtils.getDirSize(SDCardUtils.getSDCardPath() + "doctor.zip");
//                                if (!StringUtils.equals(size, "0")) {
                            //通知Activity病历打包已经完成
                            //动态注册广播
//                                    Intent intent = new Intent(ProgressBarActivity.HAVE_IMG);
//                                    LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
//                                } else {
//                                    //删除zip包
//                                    boolean deleteFile = FileUtils.deleteDir(SDCardUtils.getSDCardPath() + "doctor.zip");
//                                    if (deleteFile) {
//                                        //动态注册广播
//                                        Intent intent = new Intent(ProgressBarActivity.HAVE_NOTHING);
//                                        LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
//                                    }
//                                }
                            //                       }
                        }
                    }
                } else {
                    mFlag.add(false);
                }
            }
            if (isSample()) {
                //动态注册广播
                Intent intent = new Intent(ProgressBarActivity.HAVE_NOTHING);
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            } else if (zip) {
                Intent intent = new Intent(ProgressBarActivity.HAVE_IMG);
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            }
            return true;
        }

        private boolean isSample() {
            if (mFlag.size() > 0) {
                for (boolean flag : mFlag) {
                    if (flag) {
                        return false;
                    }
                }
            }
            return true;
        }

        private void uploadData() {
            Gson gson = new Gson();
            String json = gson.toJson(mList);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
            // 封装请求体
            MultipartBody.Part part = DocUtils.prepareFilePart(SDCardUtils.getSDCardPath() + "doctor.zip");
            HttpRequestClient.getInstance(UpdateService.this).createBaseApi().uploadJsonFile(""
                    , body, part, new BaseObserver<ResponseBody>(UpdateService.this) {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtil.showLong(RealDocApplication.getContext(), e.getMessage());
                            Log.d(TAG, e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        protected void onHandleSuccess(ResponseBody responseBody) {
                            String data = null;
                            String msg = null;
                            String code = null;
                            try {
                                data = responseBody.string().toString();
                                try {
                                    JSONObject object = new JSONObject(data);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mList = intent.getParcelableArrayListExtra("mList");
        }
        Message m = Message.obtain();
        handler.sendMessage(m);
        return START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        jobFinished(params, false);//任务执行完后记得调用jobFinsih通知系统释放相关资源
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeCallbacksAndMessages(null);
        Log.i(TAG, "onStopJob:" + params.getJobId());
        return false;
    }
}
