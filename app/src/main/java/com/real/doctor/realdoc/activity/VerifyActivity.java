package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.UserFragment;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CheckPhoneUtils;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.SpinerPopWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class VerifyActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.identify)
    EditText identify;
    @BindView(R.id.identify_text)
    TextView identifyText;
    private boolean getList = false;
    private SpinerPopWindow<String> mSpinerPopWindow;
    private List<String> list;


    @Override
    public int getLayoutId() {
        return R.layout.activity_verify;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(VerifyActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("实名认证");
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                getList = bundle.getBoolean("get_list", false);
            }
        }
        list = new ArrayList<String>();
        list.add("身份证");
        list.add("居民户口簿");
        list.add("护照");
        list.add("军官证");
        list.add("驾驶证");
        identifyText.setOnClickListener(clickListener);
        mSpinerPopWindow = new SpinerPopWindow<String>(this, list, itemClickListener);
        mSpinerPopWindow.setOnDismissListener(dismissListener);
    }

    /**
     * 监听popupwindow取消
     */
    private PopupWindow.OnDismissListener dismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
        }
    };

    /**
     * popupwindow显示的ListView的item点击事件
     */
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSpinerPopWindow.dismiss();
            identifyText.setText(list.get(position));
        }
    };

    /**
     * 显示PopupWindow
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.identify_text:
                    mSpinerPopWindow.setWidth(identifyText.getWidth());
                    mSpinerPopWindow.showAsDropDown(identifyText);
                    break;
            }
        }
    };

    private void checkIdentify() {
        String mobilePhone = mobile.getText().toString().trim();
        String name = userName.getText().toString();
        String identityCode = identify.getText().toString().trim();
        JSONObject json = null;
        if (CheckPhoneUtils.isPhone(mobilePhone) && EmptyUtils.isNotEmpty(mobilePhone)) {
            if (EmptyUtils.isNotEmpty(name)) {
                json = new JSONObject();
                try {
                    json.put("mobilePhone", mobilePhone);
                    json.put("realName", name);
                    json.put("idNo", identityCode);
                    String identify = identifyText.getText().toString();
                    if (StringUtils.equals(identify, "身份证") || StringUtils.equals(identify, "身份证(可选)")) {
                        json.put("typeId", "01");
                    } else if (StringUtils.equals(identify, "居民户口簿")) {
                        json.put("typeId", "02");
                    } else if (StringUtils.equals(identify, "护照")) {
                        json.put("typeId", "03");
                    } else if (StringUtils.equals(identify, "军官证")) {
                        json.put("typeId", "04");
                    } else if (StringUtils.equals(identify, "驾驶证")) {
                        json.put("typeId", "05");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ToastUtil.showLong(VerifyActivity.this, "账号不能为空!");
                return;
            }

        } else {
            ToastUtil.showLong(VerifyActivity.this, "请输入正确的手机号!");
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(VerifyActivity.this).createBaseApi().json("user/certification/"
                , body, new BaseObserver<ResponseBody>(VerifyActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RealDocApplication.getContext(), "实名认证失败!");
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
                                    SPUtils.put(VerifyActivity.this, Constants.VERIFYFLAG, "1");
                                    ToastUtil.showLong(RealDocApplication.getContext(), "实名认证成功!");
                                    //广播通知我的页面刷新
                                    Intent intent = new Intent(UserFragment.VERIFY_TEXT);
                                    LocalBroadcastManager.getInstance(VerifyActivity.this).sendBroadcast(intent);
                                    if (getList) {
                                        //登录成功,获得列表数据
                                        RealDocApplication.getRecordListData();
                                        actionStart(VerifyActivity.this, RecordListActivity.class);
                                    }
                                    finish();
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "实名认证失败!");
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
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.submit, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (DocUtils.isFastClick()) {
                    checkIdentify();
                }
                break;
            case R.id.finish_back:
                finish();
                break;

        }
    }


    @Override
    public void doBusiness(Context mContext) {

    }
}
