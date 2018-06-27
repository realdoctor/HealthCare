package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserFadeActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.qq)
    EditText qq;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.user_fade)
    EditText userFade;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_fade;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(UserFadeActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("用户反馈");
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.submit, R.id.finish_back})
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.submit:
                    submitFade();
                    break;
                case R.id.finish_back:
                    finish();
                    break;
            }
        }
    }

    private void submitFade() {
        String mQq = qq.getText().toString().toString();
        String mMobile = mobile.getText().toString().toString();
        String mUserFade = userFade.getText().toString().toString();
//        if(EmptyUtils.isEmpty(mMobile)){
//            if (CheckPhoneUtils.isPhone(mobilePhone) && EmptyUtils.isNotEmpty(mobilePhone)) {
//                if (EmptyUtils.isNotEmpty(name)) {
//                    json = new JSONObject();
//                    try {
//                        json.put("mobilePhone", mobilePhone);
//                        json.put("realName", name);
//                        json.put("idNo", identityCode);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    ToastUtil.showLong(VerifyActivity.this, "账号不能为空!");
//                    return;
//                }
//
//            } else {
//                ToastUtil.showLong(VerifyActivity.this, "请输入正确的手机号!");
//                return;
//            }
//            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
//            HttpRequestClient.getInstance(VerifyActivity.this).createBaseApi().json("user/certification/"
//                    , body, new BaseObserver<ResponseBody>(VerifyActivity.this) {
//
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            ToastUtil.showLong(VerifyActivity.this, e.getMessage());
//                            Log.d(TAG, e.getMessage());
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//
//                        @Override
//                        protected void onHandleSuccess(ResponseBody responseBody) {
//                            String data = null;
//                            String msg = null;
//                            String code = null;
//                            try {
//                                data = responseBody.string().toString();
//                                try {
//                                    JSONObject object = new JSONObject(data);
//                                    if (DocUtils.hasValue(object, "msg")) {
//                                        msg = object.getString("msg");
//                                    }
//                                    if (DocUtils.hasValue(object, "code")) {
//                                        code = object.getString("code");
//                                    }
//                                    if (msg.equals("ok") && code.equals("0")) {
//                                        ToastUtil.showLong(RealDocApplication.getContext(), "实名认证成功!");
//                                        //广播通知我的页面刷新
//                                        Intent intent = new Intent(UserFragment.VERIFY_TEXT);
//                                        LocalBroadcastManager.getInstance(VerifyActivity.this).sendBroadcast(intent);
//                                        finish();
//                                    } else {
//                                        ToastUtil.showLong(RealDocApplication.getContext(), "实名认证失败!");
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    });
//        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
