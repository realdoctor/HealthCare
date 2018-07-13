package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class AlreadyVertifyActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.identify)
    TextView identify;
    @BindView(R.id.identify_text)
    TextView identifyText;
    private String userId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_already_vertify;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AlreadyVertifyActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("实名认证信息");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(AlreadyVertifyActivity.this, Constants.USER_KEY, "");
        if (EmptyUtils.isNotEmpty(userId)) {
            getVertifyInfo();
        } else {
            ToastUtil.showLong(AlreadyVertifyActivity.this, "请确认您是否已登录!");
        }
    }

    @Override
    public void initEvent() {

    }

    private void getVertifyInfo() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        HttpRequestClient.getInstance(AlreadyVertifyActivity.this).createBaseApi().get("user/certification/info"
                , param, new BaseObserver<ResponseBody>(AlreadyVertifyActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(AlreadyVertifyActivity.this, "获取用户信息失败.请确定是否已登录!");
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
                                    if (DocUtils.hasValue(obj, "typeId")) {
                                        String typeId = obj.getString("typeId");
                                        if (typeId.equals("01")) {
                                            identifyText.setText("身份证");
                                        } else if (typeId.equals("02")) {
                                            identifyText.setText("居民户口簿");
                                        } else if (typeId.equals("03")) {
                                            identifyText.setText("护照");
                                        } else if (typeId.equals("04")) {
                                            identifyText.setText("军官证");
                                        } else if (typeId.equals("05")) {
                                            identifyText.setText("驾驶证");
                                        }
                                    }
                                    if (DocUtils.hasValue(obj, "idNo")) {
                                        String idNo = obj.getString("idNo");
                                        identify.setText(idNo);
                                    }
                                    if (DocUtils.hasValue(obj, "realName")) {
                                        String realName = obj.getString("realName");
                                        userName.setText(realName);
                                    }
                                    if (DocUtils.hasValue(obj, "mobilePhone")) {
                                        String mobilePhone = obj.getString("mobilePhone");
                                        mobile.setText(mobilePhone);
                                    }
                                } else {
                                    ToastUtil.showLong(AlreadyVertifyActivity.this, "获取用户信息失败.请确定是否已登录!");
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
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
