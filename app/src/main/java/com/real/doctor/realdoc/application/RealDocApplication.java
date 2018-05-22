package com.real.doctor.realdoc.application;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.google.gson.JsonObject;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.DocDetailActivity;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.greendao.DaoMaster;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
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

public class RealDocApplication extends Application {
    private static final String TAG = "RealDocApplication";

    private RealDocApplication instance;

    private static Context mContext;

    private static DaoMaster daoMaster;

    private static DaoSession daoSession;

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
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new
                StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        getRecordListData();
    }

    /**
     * 获取DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            try {
                DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, SaveDocManager.dbName + ".db", null);
                daoMaster = new DaoMaster(helper.getWritableDatabase()); //获取未加密的数据库
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return daoMaster;
    }

    /**
     * 获取DaoSession对象
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {

        if (daoSession == null) {
            if (daoMaster == null) {
                getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
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
                                                        mInstance.insertSaveDoc(getContext(), list);
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


    public static Context getContext() {
        return mContext;
    }
}