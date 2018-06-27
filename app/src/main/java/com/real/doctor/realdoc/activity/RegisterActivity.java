package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.CountDownUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.EditTextPassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
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

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.userpassword)
    EditTextPassword userepassword;
    @BindView(R.id.button_register_login)
    Button registerLoginBtn;
    @BindView(R.id.send_verify_code)
    TextView sendVerifyCode;
    @BindView(R.id.verify_code)
    EditText verifyCode;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private CountDownUtil count;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(RegisterActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        count = new CountDownUtil(sendVerifyCode)
                .setCountDownMillis(50000L)//倒计时50000ms
                .setCountDownColor(android.R.color.holo_blue_light, android.R.color.darker_gray);//不同状态字体颜色
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.button_register_login, R.id.finish_back, R.id.send_verify_code})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.button_register_login:
                    if (NetworkUtil.isNetworkAvailable(RegisterActivity.this)) {
                        String mobilePhone = phoneNumber.getText().toString().trim();
                        String verify = verifyCode.getText().toString().trim();
                        String pwd = userepassword.getText().toString().trim();
                        register(mobilePhone, verify, pwd);
                    } else {
                        ToastUtil.showLong(RegisterActivity.this, "请链接互联网!");
                        return;
                    }
                    break;
                case R.id.finish_back:
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.send_verify_code:
                    if (NetworkUtil.isNetworkAvailable(RegisterActivity.this)) {
                        String mobilePhone = phoneNumber.getText().toString().trim();
                        getVerifyCode(mobilePhone);
                    }
                    break;
            }
        }
    }

    private void getVerifyCode(String mobilePhone) {
        Map<String, String> map = new HashMap<String, String>();
        if (EmptyUtils.isEmpty(mobilePhone)) {
            ToastUtil.showLong(RegisterActivity.this, "手机号为空!");
            return;
        }
        if (!CheckPhoneUtils.isPhone(mobilePhone)) {
            ToastUtil.showLong(RegisterActivity.this, "请输入正确的手机号!");
            return;
        }
        map.put("mobilePhone", mobilePhone);
        HttpRequestClient.getInstance(RegisterActivity.this).createBaseApi().get("user/sendCode"
                , map, new BaseObserver<ResponseBody>(RegisterActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RegisterActivity.this, "获取验证码失败!");
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        String verify = null;
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
                                        if (DocUtils.hasValue(obj, "code")) {
                                            verify = obj.getString("code");
                                        }
                                        verifyCode.setText(verify.toString().trim());
                                        verifyCode.requestFocus();
                                        verifyCode.setSelection(verifyCode.getText().length());
                                        count.start();
                                    }
                                } else {
                                    ToastUtil.showLong(RegisterActivity.this, "获取验证码失败!");
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

                    @Override

                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
                        ToastUtil.showLong(RegisterActivity.this, "用户注册失败!");
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
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                } else {
                                    ToastUtil.showLong(RegisterActivity.this, msg.toString().trim());
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
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
