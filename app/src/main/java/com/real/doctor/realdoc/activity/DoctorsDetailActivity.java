package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
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
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DocContentDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class DoctorsDetailActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.doc_info)
    TextView docInfo;
    @BindView(R.id.chat_btn)
    Button chatBtn;
    @BindView(R.id.record_btn)
    Button recordBtn;
    @BindView(R.id.registrations_btn)
    Button registrationsBtn;
    private String doctorUserId;
    private String patientRecordId;
    private String deptCode;
    private String desease;
    private Dialog mProgressDialog;
    private String doctorCode;
    private String userId;
    private String hospitalId;
    private String mobile;
    private String doctorIntro;
    private DocContentDialog dialog;
    //该医生无法预约
    private static final int REQUEST_CODE_NO_EXPERT = 0x100;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doctors_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(DoctorsDetailActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("医生简介");
        mProgressDialog = DocUtils.getProgressDialog(DoctorsDetailActivity.this, "正在加载数据....");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(DoctorsDetailActivity.this, Constants.USER_KEY, "");
        doctorUserId = getIntent().getStringExtra("doctorUserId");
        desease = getIntent().getStringExtra("desease");
        mobile = getIntent().getStringExtra("mobile");
        patientRecordId = getIntent().getStringExtra("patientRecordId");
        doctorIntro = getIntent().getStringExtra("doctorIntro");
        deptCode = getIntent().getStringExtra("deptCode");
        hospitalId = getIntent().getStringExtra("hospitalId");
        doctorCode = getIntent().getStringExtra("doctorCode");
        if (EmptyUtils.isEmpty(desease) && EmptyUtils.isEmpty(mobile) && EmptyUtils.isEmpty(patientRecordId) && EmptyUtils.isNotEmpty(doctorUserId)) {
            chatBtn.setVisibility(View.GONE);
            recordBtn.setVisibility(View.GONE);
        }
        if (EmptyUtils.isNotEmpty(doctorIntro) && EmptyUtils.isNotEmpty(deptCode) && EmptyUtils.isNotEmpty(hospitalId) && EmptyUtils.isNotEmpty(doctorCode)) {
            docInfo.setText("\u3000\u3000" + doctorIntro);
        } else {
            getDoctorDetail();
        }
    }

    private void getDoctorDetail() {
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("userId", doctorUserId);
        HttpRequestClient.getInstance(DoctorsDetailActivity.this).createBaseApi().get("doctor/getDoctorInfo"
                , param, new BaseObserver<ResponseBody>(DoctorsDetailActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        ToastUtil.showLong(DoctorsDetailActivity.this, "获取医生详情失败!");
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
                                    if (DocUtils.hasValue(obj, "doctorIntro")) {
                                        String doctorIntro = obj.getString("doctorIntro");
                                        if (EmptyUtils.isNotEmpty(doctorIntro)) {
                                            docInfo.setText("\u3000\u3000" + doctorIntro);
                                        } else {
                                            docInfo.setText("\u3000\u3000该医生无简介");
                                        }
                                    }
                                    if (DocUtils.hasValue(obj, "deptCode")) {
                                        deptCode = obj.getString("deptCode");
                                    }
                                    if (DocUtils.hasValue(obj, "doctorCode")) {
                                        doctorCode = obj.getString("doctorCode");
                                    }
                                    if (DocUtils.hasValue(obj, "hospitalId")) {
                                        hospitalId = obj.getString("hospitalId");
                                    }
                                } else {
                                    ToastUtil.showLong(DoctorsDetailActivity.this, "获取医生详情失败!");
                                }
                                mProgressDialog.dismiss();
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
    @OnClick({R.id.finish_back, R.id.chat_btn, R.id.record_btn, R.id.registrations_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.chat_btn:
                if (NetworkUtil.isNetworkAvailable(DoctorsDetailActivity.this)) {
                    //点击进入付款页面
                    Intent intent = new Intent(DoctorsDetailActivity.this, ChatPayActivity.class);
                    intent.putExtra("payType", "1");
                    intent.putExtra("doctorUserId", doctorUserId);
                    intent.putExtra("desease", desease);
                    intent.putExtra("patientRecordId", patientRecordId);
                    intent.putExtra("mobile", mobile);
                    DoctorsDetailActivity.this.startActivity(intent);
                } else {
                    ToastUtil.showLong(DoctorsDetailActivity.this, "您还未连接网络,请连接互联网!");
                    NetworkUtil.goToWifiSetting(DoctorsDetailActivity.this);
                }
                break;
            case R.id.record_btn:
                if (NetworkUtil.isNetworkAvailable(DoctorsDetailActivity.this)) {
                    //点击进入付款页面
                    Intent intent = new Intent(DoctorsDetailActivity.this, ChatPayActivity.class);
                    intent.putExtra("payType", "2");
                    intent.putExtra("doctorUserId", doctorUserId);
                    intent.putExtra("desease", desease);
                    intent.putExtra("detail", true);
                    intent.putExtra("patientRecordId", patientRecordId);
                    DoctorsDetailActivity.this.startActivity(intent);
                } else {
                    ToastUtil.showLong(DoctorsDetailActivity.this, "您还未连接网络,请连接互联网!");
                    NetworkUtil.goToWifiSetting(DoctorsDetailActivity.this);
                }
                break;
            case R.id.registrations_btn:
                if (NetworkUtil.isNetworkAvailable(DoctorsDetailActivity.this)) {
                    Intent intent = new Intent(DoctorsDetailActivity.this, OrderExpertByDateActivity.class);
                    intent.putExtra("hospitalId", hospitalId);
                    intent.putExtra("doctorCode", doctorCode);
                    intent.putExtra("deptCode", deptCode);
                    startActivityForResult(intent, REQUEST_CODE_NO_EXPERT);
                } else {
                    ToastUtil.showLong(DoctorsDetailActivity.this, "您还未连接网络,请连接互联网!");
                    NetworkUtil.goToWifiSetting(DoctorsDetailActivity.this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NO_EXPERT) {
            //如果医生无法预约,弹出对话框提示
            dialog = new DocContentDialog(DoctorsDetailActivity.this, "该医生无法预约!").builder()
                    .setCancelable(false)
                    .setCanceledOnTouchOutside(true)
                    .setConfirmBtn(new DocContentDialog.ConfirmListener() {
                        @Override
                        public void onConfirmClick() {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
