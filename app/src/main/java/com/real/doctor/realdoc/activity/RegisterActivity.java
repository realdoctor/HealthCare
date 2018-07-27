package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
                    actionStart(this, LoginActivity.class);
                    break;
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void register(final String mobilePhone, String verify, final String pwd) {
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
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                EMClient.getInstance().createAccount(mobilePhone, pwd);//同步方法
                                            } catch (HyphenateException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    ToastUtil.showLong(RegisterActivity.this, "用户注册成功!");
                                    actionStart(RegisterActivity.this, LoginActivity.class);
                                    goBackBtn();
                                } else {
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
