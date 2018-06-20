package com.real.doctor.realdoc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.map.E;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CleanMessageUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.account_set)
    LinearLayout accountSet;
    @BindView(R.id.address_set)
    LinearLayout addressSet;
    @BindView(R.id.clean_cache)
    LinearLayout cleanCache;
    @BindView(R.id.user_fade)
    LinearLayout userFade;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.login_out)
    Button loginOut;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.cache)
    TextView cache;

    private CommonDialog dialog;
    private String avator;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SettingActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("设置");
        localBroadcast();
    }

    @Override
    public void initData() {
        try {
            String size = CleanMessageUtil.getTotalCacheSize(SettingActivity.this);
            cache.setText("清除缓存" + CleanMessageUtil.getFormatSize(Double.valueOf(size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取头像
        Intent intent = getIntent();
        avator = intent.getExtras().getString("imgUrl");
        if (EmptyUtils.isNotEmpty(avator)) {
            GlideUtils.loadImageViewLoding(RealDocApplication.getContext(), avator, icon, R.mipmap.ease_default_avatar, R.mipmap.ease_default_avatar);
        }
    }

    @Override
    public void initEvent() {

    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(SettingActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountActivity.CHANGE_AVATOR);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                avator = (String) intent.getExtras().get("avator");
                GlideUtils.loadImageViewLoding(RealDocApplication.getContext(), avator, icon, R.mipmap.ease_default_avatar, R.mipmap.ease_default_avatar);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    @OnClick({R.id.finish_back, R.id.login_out, R.id.account_set, R.id.address_set, R.id.clean_cache, R.id.user_fade, R.id.about_us})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.login_out:
                //弹出框界面
                dialog = new CommonDialog(this).builder()
                        .setCancelable(false)
                        .setContent("确定要退出吗？")
                        .setCanceledOnTouchOutside(true)
                        .setCancelClickBtn(new CommonDialog.CancelListener() {

                            @Override
                            public void onCancelListener() {
                                dialog.dismiss();
                            }
                        }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                            @Override
                            public void onConfrimClick() {
                                loginOut();
                            }
                        }).show();
                break;
            case R.id.account_set:
                //账号设置
                intent = new Intent(SettingActivity.this, AccountActivity.class);
                intent.putExtra("avator", avator);
                startActivity(intent);
                break;
            case R.id.address_set:
                //地址设置
                break;
            case R.id.clean_cache:
                //清除缓存
                //弹出框界面
                dialog = new CommonDialog(this).builder()
                        .setCancelable(false)
                        .setContent("确定清除缓存吗？")
                        .setCanceledOnTouchOutside(true)
                        .setCancelClickBtn(new CommonDialog.CancelListener() {

                            @Override
                            public void onCancelListener() {
                                dialog.dismiss();
                            }
                        }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                            @Override
                            public void onConfrimClick() {
                                CleanMessageUtil.clearAllCache(SettingActivity.this);
                            }
                        }).show();
                break;
            case R.id.user_fade:
                //用户反馈
                intent = new Intent(SettingActivity.this, UserFadeActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                //关于我们
                break;
        }
    }

    private void loginOut() {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        HttpRequestClient.getInstance(SettingActivity.this).createBaseApi().json(" user/logout/"
                , body, new BaseObserver<ResponseBody>(SettingActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(SettingActivity.this, e.getMessage());
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
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "用户退出成功!");
                                    SPUtils.clear(SettingActivity.this);
                                    //环信登出
                                    loginOutHuanXin();
                                    //跳转到首页
                                    Intent intent = new Intent(SettingActivity.this, RealDocActivity.class);
                                    startActivity(intent);
                                } else {
                                    ToastUtil.showLong(SettingActivity.this, "用户退出失败!");
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

    private void loginOutHuanXin() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d(TAG, "loginOut: onSuccess");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.d(TAG, "loginOut: onError");
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}