package com.real.doctor.realdoc.application;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.GreenDaoContext;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.service.PatientListService;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * @author zhujiabin
 * @package com.example.administrator.ruiyi.application
 * @fileName ${Name}
 * @Date 2018-1-4 0004
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class RealDocApplication extends MultiDexApplication {
    private static final String TAG = "RealDocApplication";
    public static String HAVE_PATIENT_LIST = "android.intent.action.have.patient.list";
    private RealDocApplication instance;

    private static Context mContext;

    private static DaoMaster daoMaster;

    private static DaoMaster daoPatientMaster;

    private static DaoSession daoSession;

    private static DaoSession daoPatientSession;

    private static SaveDocManager mInstance;
    /**
     * 本地数据库中数据条数
     */
    private static int count;

    public RealDocApplication getInstance() {
        if (instance == null) {
            instance = this;
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        MultiDex.install(this);
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new
                StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        getRecordListData();
        //建立全局文件夹
        SDCardUtils.creatSDDir("RealDoc");
        //医生端下载病历文件后处理
//        onGetPatientList();
//        localBroadcast();
    }

    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
//        if (daoMaster == null) {
        try {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, SaveDocManager.dbName + ".db", null);
            daoMaster = new DaoMaster(helper.getWritableDatabase()); //获取未加密的数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return daoMaster;
    }

    /**
     * 获取DaoSession对象
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
//        if (daoSession == null) {
        //为了数据库与打包文件不冲突,条件必须去掉,否则数据库将出现问题
        getDaoMaster(context);
        daoSession = daoMaster.newSession();
//        }
        return daoSession;
    }

    public static void getRecordListData() {
        mInstance = SaveDocManager.getInstance(getContext());
        count = (int) mInstance.getTotalCount();
        String token = (String) SPUtils.get(getContext(), "token", "");
        String mobile = (String) SPUtils.get(getContext(), "mobile", "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(getContext(), "病历数据列表请求失败,请确定您的账户已登录!");
        }

        Map<String, String> map = new HashMap<String, String>();
//        map.put("mobilePhone", "13777850036");
        map.put("mobilePhone", mobile);
        map.put("clientNum", String.valueOf(count));
        HttpRequestClient.getInstance(getContext(), HttpNetUtil.BASE_URL, header).createBaseApi().get("patient"
                , map, new BaseObserver<ResponseBody>(getContext()) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getContext(), "获取病历列表出错!");
                    }


                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        String total = null;
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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject obj = object.getJSONObject("data");
                                        if (DocUtils.hasValue(obj, "total")) {
                                            total = obj.getString("total");
                                        }
                                        if (!StringUtils.equals(String.valueOf(count), total)) {
                                            if (DocUtils.hasValue(obj, "list")) {
                                                List<SaveDocBean> list = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), SaveDocBean.class);
                                                if (EmptyUtils.isNotEmpty(list) && list.size() > 0) {
                                                    if (EmptyUtils.isNotEmpty(mInstance)) {
                                                        List<SaveDocBean> mList = new ArrayList<>();
                                                        //因为没有病历id,所以我们只能当前时间下病历是唯一的
                                                        List<String> time = mInstance.queryTimeList(getDaoSession(getContext()));
                                                        //因为后台传过来的数据没有病历id,所以我们给他添加一个
                                                        for (int i = 0; i < list.size(); i++) {
                                                            //该时间下无病历
                                                            if (!time.contains(list.get(i).getTime())) {
                                                                list.get(i).setId(String.valueOf(Math.random()));
                                                                //插入一条病历
                                                                mList.add(list.get(i));
                                                            }
                                                        }
                                                        if (mList.size() > 0) {
                                                            mInstance.insertSaveDoc(getContext(), mList);
                                                        }
                                                        ToastUtil.showLong(getContext(), "获取病历数据列表成功!");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(getContext(), "病历数据列表请求失败!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    /**
     * 为每一个病人上传病历时创建一个数据库
     * */
    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getPatientDaoMaster(Context context, String time, String folderName) {
//        if (daoPatientMaster == null) {
        try {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(new GreenDaoContext(folderName), SaveDocManager.dbPatient + time + ".db", null);
            daoPatientMaster = new DaoMaster(helper.getWritableDatabase()); //获取未加密的数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        return daoPatientMaster;
    }

    /**
     * 获取DaoSession对象
     *
     * @param context
     * @return
     */
    public static DaoSession getPatientDaoSession(Context context, String time, String folderName) {
//        if (daoPatientSession == null) {
        //为了数据库与打包文件不冲突,条件必须去掉,否则数据库将出现问题
        getPatientDaoMaster(context, time, folderName);
        daoPatientSession = daoPatientMaster.newSession();
//        }
        return daoPatientSession;
    }

    public static Context getContext() {
        return mContext;
    }

    //医生端下载病历文件后处理
    public void onGetPatientList() {
        //开个Sevice处理接下来的任务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            Intent startServiceIntent = new Intent(this, PatientListService.class);
            startService(startServiceIntent);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), PatientListService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }

    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HAVE_PATIENT_LIST);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ToastUtil.showLong(getContext(), "病人病历数据加载完成!");
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }
}