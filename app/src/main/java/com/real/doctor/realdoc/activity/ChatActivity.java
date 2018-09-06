package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.ChatFragment;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class ChatActivity extends BaseActivity {

    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    private String toChatUsername;
    private String[] permissions;
    private String realName;
    private String mobile;
    private String originalImageUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //获取权限
        PackageManager p = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                p.checkPermission("android.permission.RECORD_AUDIO", "com.real.doctor.realdoc")
                && PackageManager.PERMISSION_GRANTED == p.checkPermission("android.permission.CAMERA", "com.real.doctor.realdoc")
                && PackageManager.PERMISSION_GRANTED == p.checkPermission("android.permission.CAMERA", "com.real.doctor.realdoc")
                && PackageManager.PERMISSION_GRANTED == p.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.real.doctor.realdoc"));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermission(permissions, 0x0001);
            }
        }
        //是否存储了本人的头像和昵称判断
        realName = (String) SPUtils.get(this, Constants.REALNAME, "");
        originalImageUrl = (String) SPUtils.get(this, Constants.ORIGINALIMAGEURL, "");
        mobile = (String) SPUtils.get(this, Constants.MOBILE, "");
        if (EmptyUtils.isEmpty(realName) || EmptyUtils.isEmpty(originalImageUrl)) {
            getUserInfo();
        }
        toChatUsername = getIntent().getExtras().getString(EaseConstant.EXTRA_USER_ID);
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
//        chatFragment.onMessageRead();
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
                if (!checkPermissions(permissions)) {
                    requestPermission(permissions, 0x0001);
                }
                break;
        }
    }

    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    private boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void getUserInfo() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobile);
        HttpRequestClient.getInstance(ChatActivity.this).createBaseApi().get("user/info"
                , param, new BaseObserver<ResponseBody>(ChatActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(ChatActivity.this, "获取用户信息失败,请确定是否已登录!");
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
                                        SPUtils.put(ChatActivity.this, Constants.REALNAME, realName);
                                    }
                                    if (DocUtils.hasValue(obj, "originalImageUrl")) {
                                        originalImageUrl = obj.getString("originalImageUrl");
                                        SPUtils.put(ChatActivity.this, Constants.ORIGINALIMAGEURL, originalImageUrl);
                                    }
                                } else {
                                    ToastUtil.showLong(ChatActivity.this, "获取用户信息失败,请确定是否已登录!");
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

}
