package com.real.doctor.realdoc.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.AccountActivity;
import com.real.doctor.realdoc.activity.AlreadyVertifyActivity;
import com.real.doctor.realdoc.activity.DocPayActivity;
import com.real.doctor.realdoc.activity.GlobeUnzipActivity;
import com.real.doctor.realdoc.activity.LoginActivity;
import com.real.doctor.realdoc.activity.MyFollowDoctorsActivity;
import com.real.doctor.realdoc.activity.MyFollowNewsActivity;
import com.real.doctor.realdoc.activity.MyRegistrationActivity;
import com.real.doctor.realdoc.activity.MyRevisitActivity;
import com.real.doctor.realdoc.activity.OrderListActivity;
import com.real.doctor.realdoc.activity.RecordListActivity;
import com.real.doctor.realdoc.activity.RecordUploadActivity;
import com.real.doctor.realdoc.activity.SettingActivity;
import com.real.doctor.realdoc.activity.UserFadeActivity;
import com.real.doctor.realdoc.activity.VerifyActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * user：lqm
 * desc：第四个模块，用户模块
 */

public class UserFragment extends BaseFragment {

    private Unbinder unbinder;
    private String token;
    private String mobile;
    private String verifyFlag = "";
    private String roleId;
    private String url;
    private String realName;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.my_appointment)
    LinearLayout myAppointment;
    @BindView(R.id.packag_history)
    LinearLayout packagHistory;
    @BindView(R.id.my_visit)
    LinearLayout myVisit;
    @BindView(R.id.my_orders)
    LinearLayout myOrders;
    @BindView(R.id.my_follow)
    LinearLayout myFollow;
    @BindView(R.id.identify)
    LinearLayout identify;
    @BindView(R.id.user_avator)
    CircleImageView userAvator;
    @BindView(R.id.inquiry_pay)
    LinearLayout inquiryPay;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.suggest_submit)
    LinearLayout suggestSubmit;
    @BindView(R.id.user_setting)
    ImageView userSetting;
    @BindView(R.id.down_record)
    LinearLayout downRecord;
    @BindView(R.id.down_record_line)
    View downRecordLine;
    @BindView(R.id.record_upload)
    LinearLayout recordUpload;
    @BindView(R.id.record_upload_line)
    View recordUploadLine;
    public static String VERIFY_TEXT = "android.intent.action.record.verify.text";
    private String originalImageUrl = "";
    private boolean isUserIn = false;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_user_layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        userName.setText("个人中心");
        token = (String) SPUtils.get(getActivity(), Constants.TOKEN, "");
        mobile = (String) SPUtils.get(getActivity(), Constants.MOBILE, "");
        verifyFlag = (String) SPUtils.get(getActivity(), Constants.VERIFYFLAG, "");
        roleId = (String) SPUtils.get(getActivity(), Constants.ROLE_ID, "");
        url = (String) SPUtils.get(getActivity(), Constants.URL, "");
        realName = (String) SPUtils.get(getActivity(), Constants.REALNAME, "");
        originalImageUrl = (String) SPUtils.get(getActivity(), Constants.ORIGINALIMAGEURL, "");
        if (EmptyUtils.isNotEmpty(token) && EmptyUtils.isNotEmpty(url)) {
            downRecord.setVisibility(View.VISIBLE);
            downRecordLine.setVisibility(View.VISIBLE);
        } else {
            downRecord.setVisibility(View.GONE);
            downRecordLine.setVisibility(View.GONE);
        }
        if (EmptyUtils.isNotEmpty(token)) {
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)   myLoginLine.getLayoutParams();
//            lp.topMargin = SizeUtils.px2dp(getActivity(), 20);
//            myLoginLine.setLayoutParams(lp);
            recordUpload.setVisibility(View.VISIBLE);
            recordUploadLine.setVisibility(View.VISIBLE);
        } else {
            recordUpload.setVisibility(View.GONE);
            recordUploadLine.setVisibility(View.GONE);
        }
        if (EmptyUtils.isNotEmpty(roleId) && roleId.equals("0")) {
            inquiryPay.setVisibility(View.GONE);
        } else if (EmptyUtils.isNotEmpty(token)) {
            inquiryPay.setVisibility(View.VISIBLE);
        } else {
            inquiryPay.setVisibility(View.GONE);
        }
        if (EmptyUtils.isNotEmpty(mobile)) {
            //实名认证
            checkName(mobile);
        }
        //获得用户信息
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            getUserInfo();
        } else {
            if (verifyFlag.equals("1")) {
                userName.setText(realName);
            } else {
                userName.setText("个人中心");
            }
            GlideUtils.loadImageViewDiskCache(RealDocApplication.getContext(), originalImageUrl, userAvator);
        }
        //获得头像
        StringBuffer sb = new StringBuffer();
        sb.append(SDCardUtils.getGlobalDir());
        sb.append("avater");
        sb.append(mobile);
        sb.append(".png");
        String avaterPath = sb.toString();
        if (FileUtils.isFileExists(avaterPath)) {
            Bitmap bitmap = ImageUtils.getSmallBitmap(avaterPath, SizeUtils.dp2px(getActivity(),
                    80), SizeUtils.dp2px(getActivity(), 80));
            userAvator.setImageBitmap(bitmap);
        }
        localBroadcast();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountActivity.CHANGE_AVATOR);
        intentFilter.addAction(VERIFY_TEXT);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(VERIFY_TEXT)) {
                    verifyFlag = (String) SPUtils.get(getActivity(), Constants.VERIFYFLAG, "");
                    getUserInfo();
                } else if (action.equals(AccountActivity.CHANGE_AVATOR)) {
                    userName.setText(realName);
                    String imageUrl = (String) intent.getExtras().get("avator");
                    Bitmap bitmap = ImageUtils.getSmallBitmap(imageUrl, SizeUtils.dp2px(getActivity(), 80), SizeUtils.dp2px(getActivity(), 80));
                    userAvator.setImageBitmap(bitmap);
//                    GlideUtils.loadImageViewLoding(getContext(), originalImageUrl, userAvator, R.mipmap.ease_default_avatar, R.mipmap.ease_default_avatar);
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void getUserInfo() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobile);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("user/info"
                , param, new BaseObserver<ResponseBody>(getActivity()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
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
                                    JSONObject obj = object.getJSONObject("data");
                                    String realName = "";
                                    if (DocUtils.hasValue(obj, "realName")) {
                                        realName = obj.getString("realName");
                                    }
                                    if (verifyFlag.equals("1")) {
                                        if (EmptyUtils.isNotEmpty(realName)) {
                                            SPUtils.put(getActivity(), Constants.REALNAME, realName);
                                            userName.setText(realName);
                                        }
                                    } else {
                                        userName.setText("个人中心");
                                    }
                                    if (DocUtils.hasValue(obj, "originalImageUrl")) {
                                        originalImageUrl = obj.getString("originalImageUrl");
                                        SPUtils.put(getActivity(), Constants.ORIGINALIMAGEURL, originalImageUrl);
                                        GlideUtils.loadImageViewDiskCache(RealDocApplication.getContext(), originalImageUrl, userAvator);
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取用户信息失败.请确定是否已登录!");
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

    @Override
    public void onResume() {
        super.onResume();
        //发送广播，关闭悬浮窗
        if (isUserIn) {
            Intent msgIntent = new Intent(HomeFragment.CLOSE_WINDOW_MANAGER);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
            isUserIn = false;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    @OnClick({R.id.identify, R.id.my_appointment, R.id.packag_history, R.id.my_visit, R.id.my_orders, R.id.my_follow, R.id.user_setting, R.id.inquiry_pay, R.id.suggest_submit, R.id.down_record, R.id.record_upload})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.identify:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (verifyFlag.equals("0")) {
                        //跳转到未实名认证界面
                        intent = new Intent(getActivity(), VerifyActivity.class);
                        startActivity(intent);
                    } else if (EmptyUtils.isEmpty(token)) {
                        //跳转到登录界面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), AlreadyVertifyActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.my_appointment:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (EmptyUtils.isNotEmpty(token)) {
                        //跳转到我的预约界面
                        intent = new Intent(getActivity(), MyRegistrationActivity.class);
                        startActivity(intent);
                    } else {
                        //跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.packag_history:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (EmptyUtils.isNotEmpty(token)) {
                        intent = new Intent(getActivity(), RecordListActivity.class);
                        startActivity(intent);
                    } else {
                        //跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.my_visit:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (EmptyUtils.isNotEmpty(token)) {
                        intent = new Intent(getActivity(), MyRevisitActivity.class);
                        startActivity(intent);
                    } else {
                        //跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.my_orders:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (EmptyUtils.isNotEmpty(token)) {
                        intent = new Intent(getActivity(), OrderListActivity.class);
                        startActivity(intent);
                    } else {
                        //跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.my_follow:
                isUserIn = true;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    if (EmptyUtils.isNotEmpty(token)) {
                        intent = new Intent(getActivity(), MyFollowDoctorsActivity.class);
                        startActivity(intent);
                    } else {
                        //跳转到登录页面
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    NetworkUtil.goToWifiSetting(getActivity());
                }
                break;
            case R.id.user_setting:
                isUserIn = true;
                //跳转到设置页面`
                intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("imgUrl", originalImageUrl);
                startActivity(intent);
                break;
            case R.id.inquiry_pay:
                isUserIn = true;
                intent = new Intent(getActivity(), DocPayActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                break;
            case R.id.suggest_submit:
                isUserIn = true;
                //用户反馈
                intent = new Intent(getActivity(), UserFadeActivity.class);
                startActivity(intent);
                break;
            case R.id.down_record:
                if (EmptyUtils.isNotEmpty(token) && EmptyUtils.isNotEmpty(url)) {
                    isUserIn = true;
                    intent = new Intent(getActivity(), GlobeUnzipActivity.class);
                    startActivity(intent);
                } else {
                    //跳转到登录页面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.record_upload:
                if (EmptyUtils.isNotEmpty(token)) {
                    isUserIn = true;
                    //病历上传
                    intent = new Intent(getActivity(), RecordUploadActivity.class);
                    startActivity(intent);
                } else {
                    //跳转到登录页面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void checkName(String mobilePhone) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobilePhone);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("user/certification/check"
                , param, new BaseObserver<ResponseBody>(getActivity()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getActivity(), "获取用户信息失败,请确定是否已登录!");
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

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
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
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "verifyFlag")) {
                                        verifyFlag = obj.getString("verifyFlag");
                                        SPUtils.put(getActivity(), Constants.VERIFYFLAG, verifyFlag);
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取用户信息失败,请确定是否已登录!");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
