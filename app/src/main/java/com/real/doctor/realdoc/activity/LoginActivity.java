package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.EditTextPassword;
import com.real.doctor.realdoc.widget.HuanXinHelper;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class LoginActivity extends BaseActivity {

    public static String RECORD_LIST_HOME = "android.intent.action.home.list";
    @BindView(R.id.user_register)
    TextView userRegister;
    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.userpassword)
    EditTextPassword userpassword;
    @BindView(R.id.login_dismiss)
    ImageView loginDismiss;
    @BindView(R.id.login_weixin)
    ImageView loginWeixin;
    @BindView(R.id.login_weibo)
    ImageView loginWeibo;
    @BindView(R.id.login_qq)
    ImageView loginQq;
    //QQ登录
    //需要腾讯提供的一个Tencent类
    private Tencent mTencent;
    //还需要一个IUiListener 的实现类（LogInListener implements IUiListener）
    private LogInListener mListener;
    //新浪微博登录
    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;
    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private SsoHandler mSsoHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //初始化一个IUiListener对象，在IUiListener接口的回调方法中获取到有关授权的某些信息
        // （千万别忘了覆写onActivityResult方法，否则接收不到回调）
        mListener = new LogInListener();
        /**
         * 新浪weibo登录
         * */
//        mSsoHandler = new SsoHandler(LoginActivity.this);
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        //首先需要用APP ID 获取到一个Tencent实例
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, this.getApplicationContext());
    }

    //确保能接收到回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    @OnClick({R.id.user_register, R.id.button_login, R.id.login_dismiss})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.user_register:
                    actionStart(LoginActivity.this, RegisterActivity.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.button_login:
                    //用户登录
                    if (NetworkUtil.isNetworkAvailable(LoginActivity.this)) {
                        String mobilePhone = phoneNumber.getText().toString().trim();
                        String pwd = userpassword.getText().toString().trim();
                        login(mobilePhone, pwd);
                    } else {
                        ToastUtil.showLong(LoginActivity.this, "请链接互联网!");
                        return;
                    }
                    break;
                case R.id.login_dismiss:
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.login_weixin:
                    //点击微信登录按钮
                    loginToWeiXin();
                    break;
                case R.id.login_qq:
                    //点击qq登录按钮
                    //调用QQ登录，用IUiListener对象作参数（点击登录按钮时执行以下语句）
                    if (!mTencent.isSessionValid()) {
                        mTencent.login(LoginActivity.this, "all", mListener);
                    }
                    break;
                case R.id.login_weibo:
                    //点击新浪登录按钮
                    //新浪登录
                    mSsoHandler.authorize(new SelfWbAuthListener());
                    break;
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void login(final String mobilePhone, final String pwd) {
        JSONObject json = null;
        if (CheckPhoneUtils.isPhone(mobilePhone) && EmptyUtils.isNotEmpty(mobilePhone)) {
            if (EmptyUtils.isNotEmpty(pwd)) {
                json = new JSONObject();
                try {
                    json.put("mobilePhone", mobilePhone);
                    json.put("pwd", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.showLong(LoginActivity.this, "密码不能为空!");
                return;
            }

        } else {
            ToastUtil.showLong(LoginActivity.this, "请输入正确的手机号!");
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(LoginActivity.this).createBaseApi().json("user/login/"
                , body, new BaseObserver<ResponseBody>(LoginActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(LoginActivity.this, e.getMessage());
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
                                    ToastUtil.showLong(RealDocApplication.getContext(), "用户登录成功!");
                                    if (DocUtils.hasValue(object, "data")) {
                                        //获取用户信息，保存token
                                        JSONObject jsonObject = object.getJSONObject("data");
                                        if (DocUtils.hasValue(jsonObject, "token")) {
                                            String token = jsonObject.getString("token");
                                            if (EmptyUtils.isNotEmpty(token)) {
                                                SPUtils.put(LoginActivity.this, "token", token);
                                            }
                                            //获取用户信息
                                            UserBean user = GsonUtil.GsonToBean(jsonObject.getJSONObject("user").toString(), UserBean.class);
                                            if (EmptyUtils.isNotEmpty(user)) {
                                                SPUtils.put(LoginActivity.this, "mobile", user.getMobile());
                                                SPUtils.put(LoginActivity.this, Constants.USER_KEY, user.getId());
                                            }
                                        }
                                        //登录成功,获得列表数据
                                        RealDocApplication.getRecordListData();
                                        loginHuanXin(mobilePhone, pwd);
                                        //通知首页刷新界面
                                        actionStart(LoginActivity.this, RealDocActivity.class);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                } else {
                                    ToastUtil.showLong(LoginActivity.this, "用户登录失败!");
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

    private void loginHuanXin(String currentUsername, String currentPassword) {
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "login: onSuccess");
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // get user's info (this should be get from App's server or 3rd party service)
                HuanXinHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
            }

            @Override
            public void onProgress(int progress, String status) {
                Log.d(TAG, "login: onProgress");
            }

            @Override
            public void onError(final int code, final String message) {
                Log.d(TAG, "login: onError: " + code);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loginToWeiXin() {
        IWXAPI mApi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
        mApi.registerApp(Constants.WX_APP_ID);
        if (mApi != null && mApi.isWXAppInstalled()) {
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test_neng";
            mApi.sendReq(req);
        } else {
            ToastUtil.showLong(this, "用户未安装微信!");
        }
    }

    private class LogInListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            ToastUtil.showLong(RealDocApplication.getContext(), "授权成功!");
            JSONObject jsonObject = (JSONObject) o;
            //设置openid和token，否则获取不到下面的信息
            initOpenidAndToken(jsonObject);
            //获取QQ用户的各信息
            String openid = null, token = null;
            try {
                openid = jsonObject.getString("openid");
                token = jsonObject.getString("access_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getUserInfo(openid, token);
        }

        @Override
        public void onError(UiError uiError) {
            ToastUtil.showLong(RealDocApplication.getContext(), "授权出错!");
        }

        @Override
        public void onCancel() {
            ToastUtil.showLong(RealDocApplication.getContext(), "授权取消!");
        }

    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String openid = jsonObject.getString("openid");
            String token = jsonObject.getString("access_token");
            String expires = jsonObject.getString("expires_in");
            mTencent.setAccessToken(token, expires);
            mTencent.setOpenId(openid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo(final String openid, final String token) {
        //sdk给我们提供了一个类UserInfo，这个类中封装了QQ用户的一些信息，我么可以通过这个类拿到这些信息
        QQToken mQQToken = mTencent.getQQToken();
        UserInfo userInfo = new UserInfo(LoginActivity.this, mQQToken);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                String nickname = "", headimgurl = "";
                JSONObject userInfoJson = (JSONObject) o;
                try {
                    nickname = userInfoJson.getString("nickname");
                    headimgurl = userInfoJson.getString("figureurl_qq_2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submitThirdLogin(token, openid, nickname, headimgurl, "qq");
            }

            @Override
            public void onError(UiError uiError) {
                ToastUtil.showLong(RealDocApplication.getContext(), "获取qq用户信息错误");
            }

            @Override
            public void onCancel() {
                ToastUtil.showLong(RealDocApplication.getContext(), "获取qq用户信息取消");
            }
        });
    }

    private void submitThirdLogin(String access_token, String openid, String nickname, String headimgurl, final String third) {
        if (NetworkUtil.isNetworkAvailable(LoginActivity.this)) {
            Map<String, String> maps = new HashMap<String, String>();
            maps.put("access_token", access_token);
            maps.put("openid", openid);
            maps.put("userfrom", third);
            maps.put("nickname", nickname);
            maps.put("head_img", headimgurl);
            HttpRequestClient.getInstance(LoginActivity.this).createBaseApi().post("Account/loginOauth"
                    , maps, new BaseObserver<ResponseBody>(LoginActivity.this) {
                        @Override
                        protected void onHandleSuccess(ResponseBody responseBody) {

                        }
                    });
        } else {
            ToastUtil.showLong(RealDocApplication.getContext(), "网络已断开，请检查您的网络!");
        }
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            LoginActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAccessToken = token;
                    if (mAccessToken.isSessionValid()) {
                        // 保存 Token 到 SharedPreferences
                        AccessTokenKeeper.writeAccessToken(LoginActivity.this, mAccessToken);
                        ToastUtil.showLong(RealDocApplication.getContext(), getResources().getString(R.string.weibosdk_demo_toast_auth_success));
                    }
                    String access_token = mAccessToken.getToken();
                    String uid = mAccessToken.getUid();

                    getUserMesg(access_token, uid);
                }
            });
        }

        @Override
        public void cancel() {
            ToastUtil.showLong(RealDocApplication.getContext(), getResources().getString(R.string.weibosdk_demo_toast_auth_canceled));
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            ToastUtil.showLong(RealDocApplication.getContext(), errorMessage.getErrorMessage());
        }
    }

    /**
     * 获取微博的个人信息
     *
     * @param access_token
     * @param uid
     */
    private void getUserMesg(final String access_token, final String uid) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String mUid = Base64.encodeToString(uid.getBytes(), Base64.DEFAULT);
                String path = "https://api.weibo.com/2/users/show.json?uid="
                        + mUid
                        + "&access_token="
                        + access_token;
                String profile_image_url = "", name = "";
//                String data = mPlayUtil.loginByGet(path);
//                if (EmptyUtils.isNotEmpty(data)) {
//                    JSONObject object = null;
//                    try {
//                        object = new JSONObject(data);
//                        if (GameUtils.hasValue(object, "name")) {
//                            name = object.getString("name");
//                        }
//                        if (GameUtils.hasValue(object, "profile_image_url")) {
//                            profile_image_url = object.getString("profile_image_url");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                submitThirdLogin(access_token, uid, name, profile_image_url, "xl");
            }
        });
        thread.start();
    }

}
