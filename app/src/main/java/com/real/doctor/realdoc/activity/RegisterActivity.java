package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;

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
 * Created by Administrator on 2018/4/18.
 */

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.pwd)
    EditText pwd;
    @BindView(R.id.pwd_confirm)
    EditText pwdConfirm;
    @BindView(R.id.register_btn)
    Button registerBtn;
    @BindView(R.id.login_btn)
    TextView loginBtn;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private String mobilePhone;
    private String verify;
    private Dialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mobilePhone = intent.getExtras().getString("mobile");
            verify = intent.getExtras().getString("verify");
        }
        mProgressDialog = DocUtils.getProgressDialog(RegisterActivity.this, "注册中，请稍后...");
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.register_btn, R.id.login_btn})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.register_btn:
                    if (NetworkUtil.isNetworkAvailable(RegisterActivity.this)) {
                        String mPwd = pwd.getText().toString().trim();
                        String mConfirmPwd = pwdConfirm.getText().toString().trim();
                        if (EmptyUtils.isEmpty(mPwd)) {
                            ToastUtil.showLong(RegisterActivity.this, "密码为空!");
                            return;
                        }
                        if (EmptyUtils.isEmpty(mConfirmPwd)) {
                            ToastUtil.showLong(RegisterActivity.this, "确认密码为空!");
                            return;
                        }
                        if (mPwd.equals(mConfirmPwd)) {
                            register(mobilePhone, verify, mPwd);
                        }
                    } else {
                        ToastUtil.showLong(RegisterActivity.this, "请链接互联网!");
                        return;
                    }
                    break;
                case R.id.finish_back:
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.login_btn:
                    goBackBtn();
                    break;
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void register(final String mobilePhone, String verify, final String pwd) {
        mProgressDialog.show();
        JSONObject json = null;
        if (EmptyUtils.isEmpty(verify)) {
            ToastUtil.showLong(RegisterActivity.this, "验证码不能为空!");
            return;
        }
        if (CheckPhoneUtils.isPhone(mobilePhone) && EmptyUtils.isNotEmpty(mobilePhone)) {
            if (EmptyUtils.isNotEmpty(pwd)) {
                json = new JSONObject();
                try {
                    json.put("mobilePhone", mobilePhone);
                    json.put("verifyCode", verify);
                    json.put("pwd", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.showLong(RegisterActivity.this, "密码不能为空!");
                return;
            }

        } else {
            ToastUtil.showLong(RegisterActivity.this, "请输入正确的手机号!");
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(RegisterActivity.this).createBaseApi().json("user/regist/"
                , body, new BaseObserver<ResponseBody>(RegisterActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        ToastUtil.showLong(RegisterActivity.this, "用户注册失败!");
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
                                    //环信用户注册
                                    //注册失败会抛出HyphenateException
                                    registerHuanXin(mobilePhone, pwd);
                                    ToastUtil.showLong(RegisterActivity.this, "用户注册成功!");
                                    mProgressDialog.dismiss();
                                    actionStart(RegisterActivity.this, LoginActivity.class);
                                    goBackBtn();
                                } else {
                                    mProgressDialog.dismiss();
                                    ToastUtil.showLong(RegisterActivity.this, msg.toString().trim());
                                    goBackBtn();
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

    private void registerHuanXin(final String mobilePhone, final String pwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mobilePhone, pwd);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showLong(RegisterActivity.this, "环信聊天注册成功");
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            Log.d("lzan13", String.format("sign up - errorCode:%d, errorMsg:%s", errorCode, e.getMessage()));
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    ToastUtil.showLong(RegisterActivity.this, "网络错误 code: " + errorCode + ", message:" + message);
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    ToastUtil.showLong(RegisterActivity.this, "用户已存在 code: " + errorCode + ", message:" + message);
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    ToastUtil.showLong(RegisterActivity.this, "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: " + errorCode + ", message:" + message);
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    ToastUtil.showLong(RegisterActivity.this, "服务器未知错误 code: " + errorCode + ", message:" + message);
                                    break;
                                case EMError.USER_REG_FAILED:
                                    ToastUtil.showLong(RegisterActivity.this, "账户注册失败 code: " + errorCode + ", message:" + message);
                                    break;
                                default:
                                    ToastUtil.showLong(RegisterActivity.this, "ml_sign_up_failed code: " + errorCode + ", message:" + message);
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void goBackBtn() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
