package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.EditTextPassword;

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

    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.userpassword)
    EditTextPassword userepassword;
    @BindView(R.id.button_register_login)
    Button registerLoginBtn;
    @BindView(R.id.finish_back)
    ImageView finishBack;

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

    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.button_register_login, R.id.finish_back})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.button_register_login:
                    if (NetworkUtil.isNetworkAvailable(RegisterActivity.this)) {
                        String mobilePhone = phoneNumber.getText().toString().trim();
                        String pwd = userepassword.getText().toString().trim();
                        register(mobilePhone, pwd);
                    } else {
                        ToastUtil.showLong(RegisterActivity.this, "请链接互联网!");
                        return;
                    }
                    break;
                case R.id.finish_back:
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;

            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void register(String mobilePhone, String pwd) {
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
                                    ToastUtil.showLong(RegisterActivity.this, "用户注册成功!");
                                    actionStart(RegisterActivity.this, LoginActivity.class);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                } else {
                                    ToastUtil.showLong(RegisterActivity.this, "用户注册失败!");
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
