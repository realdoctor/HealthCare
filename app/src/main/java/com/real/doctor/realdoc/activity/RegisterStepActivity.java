package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.CountDownUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class RegisterStepActivity extends BaseActivity {

    @BindView(R.id.phone_number)
    EditText phoneNumber;
    @BindView(R.id.verify_code)
    EditText verifyCode;
    private CountDownUtil count;
    @BindView(R.id.send_verify_code)
    TextView sendVerifyCode;
    @BindView(R.id.next_step_btn)
    Button nextStepBtn;
    @BindView(R.id.login_btn)
    TextView loginBtn;
    private String mobilePhone;
    private String verify;

    @Override
    public int getLayoutId() {
        return R.layout.activity_step_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
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
    @OnClick({R.id.next_step_btn, R.id.finish_back, R.id.send_verify_code})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.next_step_btn:
                    mobilePhone = phoneNumber.getText().toString().trim();
                    verify = verifyCode.getText().toString().trim();
                    if (EmptyUtils.isEmpty(mobilePhone)) {
                        ToastUtil.showLong(RegisterStepActivity.this, "手机号为空,请输入的手机号!!");
                        return;
                    }
                    if (EmptyUtils.isEmpty(verify)) {
                        ToastUtil.showLong(RegisterStepActivity.this, "验证码为空,请输入的验证码!");
                        return;
                    }
                    if (!CheckPhoneUtils.isPhone(mobilePhone)) {
                        ToastUtil.showLong(RegisterStepActivity.this, "请输入正确的手机号!");
                        return;
                    }
                    Intent intent = new Intent(this, RegisterActivity.class);
                    intent.putExtra("mobile", mobilePhone);
                    intent.putExtra("verify", verify);
                    startActivity(intent);
                    break;
                case R.id.finish_back:
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                case R.id.send_verify_code:
                    if (NetworkUtil.isNetworkAvailable(RegisterStepActivity.this)) {
                        mobilePhone = phoneNumber.getText().toString().trim();
                        getVerifyCode(mobilePhone);
                    }
                    break;
                case R.id.login_btn:
                    actionStart(this, LoginActivity.class);
                    break;
            }
        }
    }

    private void getVerifyCode(String mobilePhone) {
        Map<String, String> map = new HashMap<String, String>();
        if (EmptyUtils.isEmpty(mobilePhone)) {
            ToastUtil.showLong(RegisterStepActivity.this, "手机号为空!");
            return;
        }
        if (!CheckPhoneUtils.isPhone(mobilePhone)) {
            ToastUtil.showLong(RegisterStepActivity.this, "请输入正确的手机号!");
            return;
        }
        map.put("mobilePhone", mobilePhone);
        HttpRequestClient.getInstance(RegisterStepActivity.this).createBaseApi().get("user/sendCode"
                , map, new BaseObserver<ResponseBody>(RegisterStepActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RegisterStepActivity.this, "获取验证码失败!");
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
                                    ToastUtil.showLong(RegisterStepActivity.this, "获取验证码失败!");
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
}
