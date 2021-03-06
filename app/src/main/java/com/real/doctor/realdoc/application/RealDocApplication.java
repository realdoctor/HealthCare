package com.real.doctor.realdoc.application;

import android.app.Activity;
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

//import com.huawei.android.hms.agent.HMSAgent;
//import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.GreenDaoContext;
import com.real.doctor.realdoc.greendao.table.DrugManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.DrugBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.service.PatientListService;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.HuanXinHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
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

    private static DaoMaster daoGlobeMaster;

    private static DaoSession daoSession;

    private static DaoSession daoPatientSession;

    private static DaoSession daoGlobeSession;
    private static SaveDocManager mInstance;
    private static DrugManager mDrugInstance;
    /**
     * 本地数据库中数据条数
     */
    private static int count;

    public static boolean ignoreMobile;

    private String verifyFlag = "";

    public static final int PLAN_ID_IJK = 1;
    public static final int PLAN_ID_EXO = 2;

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
        verifyFlag = (String) SPUtils.get(mContext, Constants.VERIFYFLAG, "");
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            if (StringUtils.equals(verifyFlag, "1")) {
                getRecordListData();
            } else {
                //do nothing
            }
        } else {
            NetworkUtil.goToWifiSetting(getContext());
        }
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //init demo helper
        HuanXinHelper.getInstance().init(getContext());
        //华为推送初始化
//        HMSAgent.init(this);
//        HMSAgent.connect((Activity) getContext(), new ConnectHandler() {
//            @Override
//            public void onConnect(int rst) {
////                showLog("HMS connect end:" + rst);
//            }
//        });
        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_IJK, IjkPlayer.class.getName(), "IjkPlayer"));
        PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);
        //use default NetworkEventProducer.
        PlayerConfig.setUseDefaultNetworkEventProducer(true);

        PlayerLibrary.init(this);

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
        mDrugInstance = DrugManager.getInstance(getContext());
        count = (int) mInstance.getTotalCount();
        String token = (String) SPUtils.get(getContext(), Constants.TOKEN, "");
        String mobile = (String) SPUtils.get(getContext(), Constants.MOBILE, "");
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
        HttpRequestClient.getInstance(getContext()).createBaseApi().get("patient/list"
                , map, new BaseObserver<ResponseBody>(getContext()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getContext(), "获取病历列表出错!");
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
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
                                        //因为没有病历id,所以我们只能当前时间下病历是唯一的
                                        List<String> time = mInstance.queryTimeList(getDaoSession(getContext()));
                                        if (!StringUtils.equals(String.valueOf(count), total)) {
                                            if (DocUtils.hasValue(obj, "list")) {
                                                JSONArray jsonArray = obj.getJSONArray("list");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    SaveDocBean bean = new SaveDocBean();
                                                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                    if (DocUtils.hasValue(jsonObj, "diagCode")) {
                                                        String diagCode = jsonObj.getString("diagCode");
                                                        bean.setId(diagCode);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "diagName")) {
                                                        String diagName = jsonObj.getString("diagName");
                                                        bean.setIll(diagName);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "orgCode")) {
                                                        String orgCode = jsonObj.getString("orgCode");
                                                        bean.setOrgCode(orgCode);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "doctorUserId")) {
                                                        String doctorUserId = jsonObj.getString("doctorUserId");
                                                        bean.setDoctorUserId(doctorUserId);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "patientDiagId")) {
                                                        String patientDiagId = jsonObj.getString("patientDiagId");
                                                        bean.setPatientDiagId(patientDiagId);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "patientId")) {
                                                        String patientId = jsonObj.getString("patientId");
                                                        bean.setPatientId(patientId);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "respDoctorName")) {
                                                        String respDoctorName = jsonObj.getString("respDoctorName");
                                                        bean.setDoctor(respDoctorName);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "visitDeptName")) {
                                                        String visitDeptName = jsonObj.getString("visitDeptName");
                                                        bean.setVisitDeptName(visitDeptName);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "visitDtime")) {
                                                        String visitDtime = jsonObj.getString("visitDtime");
                                                        bean.setTime(visitDtime);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "visitOrgName")) {
                                                        String visitOrgName = jsonObj.getString("visitOrgName");
                                                        bean.setHospital(visitOrgName);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "visitWay")) {
                                                        String visitWay = jsonObj.getString("visitWay");
                                                        bean.setVisitWay(visitWay);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "patientRecordId")) {
                                                        String patientRecordId = jsonObj.getString("patientRecordId");
                                                        bean.setPatientRecordId(patientRecordId);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "mobilePhone")) {
                                                        String mobilePhone = jsonObj.getString("mobilePhone");
                                                        bean.setMobilePhone(mobilePhone);
                                                    }
                                                    //插入到数据库中
                                                    if (!time.contains(bean.getTime())) {
                                                        bean.setId(String.valueOf(Math.random()));
                                                        //插入一条病历
                                                        mInstance.insertSaveDoc(getContext(), bean);
                                                        //插入药物处方
                                                        if (DocUtils.hasValue(jsonObj, "drugList")) {
                                                            JSONArray array = jsonObj.getJSONArray("drugList");
                                                            for (int j = 0; j < array.length(); j++) {
                                                                DrugBean drugBean = new DrugBean();
                                                                JSONObject jsonObject = array.getJSONObject(j);
                                                                drugBean.setRecordId(bean.getId());
                                                                if (DocUtils.hasValue(jsonObject, "drugCode")) {
                                                                    String drugCode = jsonObject.getString("drugCode");
                                                                    if (EmptyUtils.isNotEmpty(drugCode)) {
                                                                        drugBean.setDrugCode(drugCode);
                                                                    } else {
                                                                        drugBean.setDrugCode("null");
                                                                    }
                                                                }
                                                                if (DocUtils.hasValue(jsonObject, "drugName")) {
                                                                    String drugName = jsonObject.getString("drugName");
                                                                    drugBean.setDrugName(drugName);
                                                                }
                                                                if (DocUtils.hasValue(jsonObject, "drugStdCode")) {
                                                                    String drugStdCode = jsonObject.getString("drugStdCode");
                                                                    drugBean.setDrugStdCode(drugStdCode);
                                                                }
                                                                if (DocUtils.hasValue(jsonObject, "drugStdName")) {
                                                                    String drugStdName = jsonObject.getString("drugStdName");
                                                                    drugBean.setDrugStdName(drugStdName);
                                                                }
                                                                mDrugInstance.insertDrug(getContext(), drugBean);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.showLong(getContext(), "获取病历数据列表成功!");
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
    /**
     * 为每一个病人上传病历时创建一个数据库
     * */
    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getGlobeDaoMaster(Context context, String mobile, String folderName) {
        try {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(new GreenDaoContext(folderName), mobile + ".db", null);
            daoGlobeMaster = new DaoMaster(helper.getWritableDatabase()); //获取未加密的数据库
        } catch (Exception e) {
            e.printStackTrace();
        }
        return daoGlobeMaster;
    }

    /**
     * 获取DaoSession对象
     *
     * @param context
     * @return
     */
    public static DaoSession getGlobeDaoSession(Context context, String mobile, String folderName) {
        //为了数据库与打包文件不冲突,条件必须去掉,否则数据库将出现问题
        getGlobeDaoMaster(context, mobile, folderName);
        daoGlobeSession = daoGlobeMaster.newSession();
        return daoGlobeSession;
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