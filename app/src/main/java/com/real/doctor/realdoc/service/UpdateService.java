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
import com.real.doctor.realdoc.activity.DocCompareActivity;
import com.real.doctor.realdoc.activity.ProgressBarActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
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
    private List<String> mImgList = new ArrayList<>();
    private List<Boolean> mFlag = new ArrayList<>();
    private boolean zip = false;
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mFlag.clear();
            for (SaveDocBean bean : mList) {
                String mId = bean.getId();
                if (EmptyUtils.isNotEmpty(mId)) {
                    List<String> idOneList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(UpdateService.this), mId);
                    for (int i = 0; i < idOneList.size(); i++) {
                        List<String> mImageUrlList = imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(UpdateService.this), idOneList.get(i));
                        mImgList.addAll(mImageUrlList);
                    }
                    if (mImgList.size() == 0) {
                        mFlag.add(false);
                    }else{
                        mFlag.add(true);
                    }
                    if (SDCardUtils.isSDCardEnable()) {
                        //新建doctor文件夹
                        String folderName = SDCardUtils.getSDCardPath() + "doctor" + File.separator;
                        File file = new File(folderName);
                        if (!file.exists())
                            file.mkdir();  //如果不存在则创建
                        boolean isEmptyFolder = FileUtils.deleteAllInDir(SDCardUtils.getSDCardPath() + "doctor");
                        if (isEmptyFolder) {
                            for (int i = 0; i < mImgList.size(); i++) {
                                String img = mImgList.get(i);
                                Bitmap bitmap = BitmapFactory.decodeFile(img);
                                if (EmptyUtils.isNotEmpty(bitmap)) {
                                    SDCardUtils.saveToSdCard(folderName, bitmap, img.substring(img.lastIndexOf("/") + 1, img.length()));
                                }
                            }
                            zip = false;
                            //打包
                            try {
                                zip = ZipUtils.zipFile(folderName, SDCardUtils.getSDCardPath() + "doctor.zip", "doctor");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (isTrue() && zip) {
                //动态注册广播
                Intent intent = new Intent(ProgressBarActivity.HAVE_IMG);
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            } else {
                Intent intent = new Intent(ProgressBarActivity.HAVE_NOTHING);
                LocalBroadcastManager.getInstance(UpdateService.this).sendBroadcast(intent);
            }
            return true;
        }

        private boolean isTrue() {
            if (mFlag.size() > 0) {
                for (boolean flag : mFlag) {
                    if (flag) {
                        return true;
                    }
                }
            }
            return false;
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
        imageInstance = ImageManager.getInstance(UpdateService.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(UpdateService.this);
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
