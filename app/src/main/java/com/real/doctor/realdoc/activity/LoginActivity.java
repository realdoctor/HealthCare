package com.real.doctor.realdoc.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.icu.text.AlphabeticIndex;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.ImageBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.greendao.VideoBeanDao;
import com.real.doctor.realdoc.greendao.table.DrugManager;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.DrugBean;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.UserBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CommonDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.user_register)
    TextView userRegister;
    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.userpassword)
    EditText userpassword;
    //    @BindView(R.id.login_dismiss)
//    ImageView loginDismiss;
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
    private String verifyFlag = "";
    private SaveDocManager mInstance;
    private DrugManager mDrugInstance;
    /**
     * 本地数据库中数据条数
     */
    private int count;
    private String path;
    private SaveDocManager instance;
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    //外部新增的病历
    private List<SaveDocBean> lastList;
    private List<RecordBean> lastRecordList;
    private List<VideoBean> lastVideoList;
    private List<ImageListBean> lastImageRvList;
    private List<ImageBean> lastImageList;
    private CommonDialog dialog;
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    private boolean getList = false;

    @Override
    public void initData() {
        //初始化一个IUiListener对象，在IUiListener接口的回调方法中获取到有关授权的某些信息
        // （千万别忘了覆写onActivityResult方法，否则接收不到回调）
        mListener = new LogInListener();
        /**
         * 新浪weibo登录
         * */
//        mSsoHandler = new SsoHandler(LoginActivity.this);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                getList = bundle.getBoolean("get_list", false);
            }
        }
        instance = SaveDocManager.getInstance(LoginActivity.this);
        imageInstance = ImageManager.getInstance(LoginActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(LoginActivity.this);
        recordInstance = RecordManager.getInstance(LoginActivity.this);
        videoInstance = VideoManager.getInstance(LoginActivity.this);
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

//    //确保能接收到回调
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
//        if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
//    }

    @Override
    @OnClick({R.id.user_register, R.id.button_login})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.user_register:
                    actionStart(LoginActivity.this, RegisterStepActivity.class);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(LoginActivity.this, "用户登录失败!");
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
                                    ToastUtil.showLong(LoginActivity.this, "用户登录成功!");
                                    if (DocUtils.hasValue(object, "data")) {
                                        String token = "";
                                        //获取用户信息，保存token
                                        final JSONObject jsonObject = object.getJSONObject("data");
                                        if (DocUtils.hasValue(jsonObject, "url")) {
                                            String url = jsonObject.getString("url");
                                            SPUtils.put(LoginActivity.this, Constants.URL, url);
                                        }
                                        if (DocUtils.hasValue(jsonObject, "token")) {
                                            token = jsonObject.getString("token");
                                            if (EmptyUtils.isNotEmpty(token)) {
                                                SPUtils.put(LoginActivity.this, Constants.TOKEN, token);
                                            }
                                            //获取用户信息
                                            final UserBean user = GsonUtil.GsonToBean(jsonObject.getJSONObject("user").toString(), UserBean.class);
                                            if (EmptyUtils.isNotEmpty(user)) {
                                                SPUtils.put(LoginActivity.this, Constants.MOBILE, user.getMobile());
                                                SPUtils.put(LoginActivity.this, Constants.USER_KEY, user.getId());
                                                SPUtils.put(LoginActivity.this, Constants.ROLE_ID, user.getRoleId());
                                                //设置极光推送别名
                                                // 调用 Handler 来异步设置别名
                                                mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, user.getId()));
                                                //从主数据中提取出新增的数据,如果有数据的话,弹出对话框,没有则不弹出
                                                lastList = instance.querySaveDocListByFolder(LoginActivity.this);
                                                //是否该账号有外部数据库，如果有则将外部数据库导入现在的数据库中,否则就不做处理
                                                StringBuffer sb = new StringBuffer();
                                                sb.append(SDCardUtils.getGlobalDir());
                                                sb.append("datebases");
                                                sb.append(File.separator);
                                                sb.append(user.getMobile() + ".db");
                                                path = sb.toString();
                                                List<String> idList = null;
                                                boolean isNotContains = false;
                                                if (FileUtils.isFileExists(path)) {
                                                    //将外部数据库导入内部,并删除内部数据库
                                                    idList = instance.queryGlobeList(RealDocApplication.getGlobeDaoSession(LoginActivity.this, user.getMobile(), SDCardUtils.getGlobalDir()));
                                                    for (int i = 0; i < lastList.size(); i++) {
                                                        if (!idList.contains(lastList.get(i).getId())) {
                                                            isNotContains = true;
                                                            break;
                                                        }
                                                    }
                                                    if (lastList.size() > 0 && isNotContains) {
                                                        isShowDialog(token, mobilePhone, pwd);
                                                    } else {
                                                        getExternalData(user.getMobile());
                                                        //融合下载下来的病历
//                                                    getDownData(jsonObject);
                                                        //实名认证
                                                        checkName(mobilePhone, pwd, token);
                                                    }
                                                } else {
                                                    if (lastList.size() > 0) {
                                                        isShowDialog(token, mobilePhone, pwd);
                                                    } else {
                                                        getExternalData(user.getMobile());
                                                        //融合下载下来的病历
//                                                    getDownData(jsonObject);
                                                        //实名认证
                                                        checkName(mobilePhone, pwd, token);
                                                    }
                                                }
                                            }
                                        }
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

    private void isShowDialog(String token, final String mobilePhone, final String pwd) {
        //弹出框界面
        final String finalToken = token;
        dialog = new CommonDialog(LoginActivity.this).builder()
                .setCancelable(false)
                .setContent("病历列表中有新增的病历,是否添加到该账户中？")
                .setCanceledOnTouchOutside(true)
                .setCancelClickBtn(new CommonDialog.CancelListener() {

                    @Override
                    public void onCancelListener() {

                        getExternalData(mobilePhone);
                        //融合下载下来的病历
                        //getDownData(jsonObject);
                        //实名认证
                        checkName(mobilePhone, pwd, finalToken);
                        dialog.dismiss();
                    }
                }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                    @Override
                    public void onConfrimClick() {
                        //在这种情况下,视频,音频,图片只有未登录时的数据库才会有,所以无脑地查询就行了
                        lastRecordList = recordInstance.queryRecordList(LoginActivity.this);
                        lastVideoList = videoInstance.queryVideoList(LoginActivity.this);
                        lastImageRvList = imageRecycleInstance.queryImageListList(LoginActivity.this);
                        lastImageList = imageInstance.queryImageList(LoginActivity.this);
                        //将该数据放到外部数据库中,退出的时候要归还给未登录时候的数据库的
                        setExternalData("external");
                        getExternalData(mobilePhone);
                        //融合新增数据库中的病历
                        instance.insertSaveDoc(LoginActivity.this, lastList);
                        imageInstance.insertImageList(LoginActivity.this, lastImageList);
                        imageRecycleInstance.insertImageListList(LoginActivity.this, lastImageRvList);
                        recordInstance.insertRecordList(LoginActivity.this, lastRecordList);
                        videoInstance.insertVideoList(LoginActivity.this, lastVideoList);
                        //融合下载下来的病历
                        //getDownData(jsonObject);
                        //实名认证
                        checkName(mobilePhone, pwd, finalToken);
                    }
                }).show();
    }

    private void getExternalData(String mobile) {
        //是否该账号有外部数据库，如果有则将外部数据库导入现在的数据库中,否则就不做处理
//        StringBuffer sb = new StringBuffer();
//        sb.append(SDCardUtils.getGlobalDir());
//        sb.append("datebases");
//        sb.append(File.separator);
//        sb.append(mobile + ".db");
//        path = sb.toString();
        if (FileUtils.isFileExists(path)) {
            //将外部数据库导入内部,并删除内部数据库
            List<SaveDocBean> list = instance.queryGlobeSaveDocList(LoginActivity.this, mobile, SDCardUtils.getGlobalDir());
            instance.insertGlobedSaveDoc(LoginActivity.this, list);
            List<ImageBean> beanList = imageInstance.queryGlobeImage(LoginActivity.this, mobile, SDCardUtils.getGlobalDir());
            imageInstance.insertGlobedImageList(LoginActivity.this, beanList);
            List<ImageListBean> imageList = imageRecycleInstance.queryGlobeImageList(LoginActivity.this, mobile, SDCardUtils.getGlobalDir());
            imageRecycleInstance.insertGlobedImageListList(LoginActivity.this, imageList);
            List<RecordBean> recordList = recordInstance.queryGlobeRecord(LoginActivity.this, mobile, SDCardUtils.getGlobalDir());
            recordInstance.insertGlobedRecordList(LoginActivity.this, recordList);
            List<VideoBean> videoList = videoInstance.queryGlobeVideo(LoginActivity.this, mobile, SDCardUtils.getGlobalDir());
            videoInstance.insertGlobedVideoList(LoginActivity.this, videoList);
            //删除数据库
            FileUtils.deleteDir(path);
        } else {
            SaveDocBeanDao saveDocDao = RealDocApplication.getDaoSession(LoginActivity.this).getSaveDocBeanDao();
            saveDocDao.deleteAll();
            ImageListBeanDao imageListBeanDao = RealDocApplication.getDaoSession(LoginActivity.this).getImageListBeanDao();
            imageListBeanDao.deleteAll();
            ImageBeanDao imageBeanDao = RealDocApplication.getDaoSession(LoginActivity.this).getImageBeanDao();
            imageBeanDao.deleteAll();
            VideoBeanDao videoBeanDao = RealDocApplication.getDaoSession(LoginActivity.this).getVideoBeanDao();
            videoBeanDao.deleteAll();
            RecordBeanDao recordBeanDao = RealDocApplication.getDaoSession(LoginActivity.this).getRecordBeanDao();
            recordBeanDao.deleteAll();
        }
    }

    private void setExternalData(String external) {
        //将数据库导出到文件夹中,并且覆盖文件夹中的数据库
        List<SaveDocBean> list = instance.querySaveDocListByFolder(LoginActivity.this);
        instance.insertGlobeSaveDoc(LoginActivity.this, list, external, SDCardUtils.getGlobalDir());
        //外部数据库如果是上面的话,没有这些文件存在的,所以可以全部一起插入
        List<ImageBean> beanList = imageInstance.queryImageList(LoginActivity.this);
        imageInstance.insertGlobeImageList(LoginActivity.this, beanList, external, SDCardUtils.getGlobalDir());
        List<ImageListBean> imageList = imageRecycleInstance.queryImageListList(LoginActivity.this);
        imageRecycleInstance.insertGlobelImageListList(LoginActivity.this, imageList, external, SDCardUtils.getGlobalDir());
        List<RecordBean> recordList = recordInstance.queryRecordList(LoginActivity.this);
        recordInstance.insertGlobeRecordList(LoginActivity.this, recordList, external, SDCardUtils.getGlobalDir());
        List<VideoBean> videoList = videoInstance.queryVideoList(LoginActivity.this);
        videoInstance.insertGlobeVideoList(LoginActivity.this, videoList, external, SDCardUtils.getGlobalDir());
    }

//    private void getDownData(JSONObject jsonObject) {
//        //融合下载下来的数据,在GlobeUnzipService中处理
//        if (DocUtils.hasValue(jsonObject, "url")) {
//            String url = null;
//            try {
//                url = jsonObject.getString("url");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            Intent intent = new Intent(LoginActivity.this, GlobeUnzipActivity.class);
//            intent.putExtra("url", url);
//            startActivity(intent);
//        }
//    }

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
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
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
            ToastUtil.showLong(LoginActivity.this, "授权成功!");
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
            ToastUtil.showLong(LoginActivity.this, "授权出错!");
        }

        @Override
        public void onCancel() {
            ToastUtil.showLong(LoginActivity.this, "授权取消!");
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
                ToastUtil.showLong(LoginActivity.this, "获取qq用户信息错误");
            }

            @Override
            public void onCancel() {
                ToastUtil.showLong(LoginActivity.this, "获取qq用户信息取消");
            }
        });
    }

    private void submitThirdLogin(String access_token, String openid, String nickname, String
            headimgurl, final String third) {
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
            ToastUtil.showLong(LoginActivity.this, "网络已断开，请检查您的网络!");
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
                        ToastUtil.showLong(LoginActivity.this, getResources().getString(R.string.weibosdk_demo_toast_auth_success));
                    }
                    String access_token = mAccessToken.getToken();
                    String uid = mAccessToken.getUid();

                    getUserMesg(access_token, uid);
                }
            });
        }

        @Override
        public void cancel() {
            ToastUtil.showLong(LoginActivity.this, getResources().getString(R.string.weibosdk_demo_toast_auth_canceled));
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            ToastUtil.showLong(LoginActivity.this, errorMessage.getErrorMessage());
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

    private void checkName(final String mobilePhone, final String pwd, String token) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobilePhone);
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(this, "病历数据列表请求失败,请确定您的账户已登录!");
        }
        HttpRequestClient.getNotInstance(LoginActivity.this, HttpNetUtil.BASE_URL, header).createBaseApi().get("user/certification/check"
                , param, new BaseObserver<ResponseBody>(LoginActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(LoginActivity.this, "获取用户信息失败.请确定是否已登录!");
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
                                        SPUtils.put(LoginActivity.this, Constants.VERIFYFLAG, verifyFlag);
                                        if (!getList && StringUtils.equals(verifyFlag, "1")) {
                                            loginHuanXin(mobilePhone, pwd);
                                            //登录成功,获得列表数据
                                            getRecordListData();
                                        } else if (getList && StringUtils.equals(verifyFlag, "0")) {
                                            loginHuanXin(mobilePhone, pwd);
                                            //跳转到实名认证页面
                                            Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                                            intent.putExtra("get_list", true);
                                            startActivity(intent);
                                        } else if (getList && StringUtils.equals(verifyFlag, "1")) {
                                            //登录成功,获得列表数据
                                            getRecordListData();
                                            loginHuanXin(mobilePhone, pwd);
                                            //通知首页刷新界面
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        } else if (!getList) {
                                            loginHuanXin(mobilePhone, pwd);
                                            //通知首页刷新界面
                                            Intent intent = new Intent(LoginActivity.this, RealDocActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(LoginActivity.this, "获取用户信息失败.请确定是否已登录!");
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

    public void getRecordListData() {
        mInstance = SaveDocManager.getInstance(LoginActivity.this);
        mDrugInstance = DrugManager.getInstance(LoginActivity.this);
        count = (int) mInstance.getTotalCount();
        String token = (String) SPUtils.get(LoginActivity.this, Constants.TOKEN, "");
        String mobile = (String) SPUtils.get(LoginActivity.this, Constants.MOBILE, "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(LoginActivity.this, "病历数据列表请求失败,请确定您的账户已登录!");
        }

        Map<String, String> map = new HashMap<String, String>();
//        map.put("mobilePhone", "13777850036");
        map.put("mobilePhone", mobile);
        map.put("clientNum", String.valueOf(count));
        HttpRequestClient.getInstance(LoginActivity.this).createBaseApi().get("patient/list"
                , map, new BaseObserver<ResponseBody>(LoginActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(LoginActivity.this, "获取病历列表出错!");
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
                                        List<String> time = mInstance.queryTimeList(RealDocApplication.getDaoSession(LoginActivity.this));
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
                                                    //插入到数据库中
                                                    if (!time.contains(bean.getTime())) {
                                                        bean.setId(String.valueOf(Math.random()));
                                                        //插入一条病历
                                                        mInstance.insertSaveDoc(LoginActivity.this, bean);
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
                                                                mDrugInstance.insertDrug(LoginActivity.this, drugBean);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.showLong(LoginActivity.this, "获取病历数据列表成功!");
                                } else {
                                    ToastUtil.showLong(LoginActivity.this, "病历数据列表请求失败!");
                                }
                                //通知首页刷新界面
                                Intent intent = new Intent(LoginActivity.this, RealDocActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
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
