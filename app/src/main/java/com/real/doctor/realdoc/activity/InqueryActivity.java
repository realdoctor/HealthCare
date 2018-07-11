package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CommonDialog;

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

public class InqueryActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.inquery_edit)
    EditText inqueryEdit;
    @BindView(R.id.next_step)
    Button nextStep;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private String doctorUserId;
    private String desease;
    private CommonDialog dialog;
    private String inqueryEditContent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_inquery;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(InqueryActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询内容");
    }

    @Override
    public void initData() {
        doctorUserId = getIntent().getStringExtra("doctorUserId");
        desease = getIntent().getStringExtra("desease");
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.next_step, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.next_step:
                inqueryEditContent = inqueryEdit.getText().toString();
                if (EmptyUtils.isNotEmpty(inqueryEditContent)) {
                    if (inqueryEditContent.length() > 10) {
                        //弹出是否需要添加相关病历资料对话框
                        //弹出框界面
                        dialog = new CommonDialog(this).builder()
                                .setCancelable(false)
                                .setContent("是否需要添加相关病历资料？")
                                .setCanceledOnTouchOutside(true)
                                .setCancelClickBtn(new CommonDialog.CancelListener() {

                                    @Override
                                    public void onCancelListener() {
                                        //调用接口,上传咨询信息
                                        postInquery();
                                    }
                                }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                                    @Override
                                    public void onConfrimClick() {
                                        //进入病历列表页面
                                        Intent intent = new Intent(InqueryActivity.this, CheckDocActivity.class);
                                        intent.putExtra("inquery", inqueryEditContent);
                                        intent.putExtra("doctorUserId", doctorUserId);
                                        intent.putExtra("desease", desease);
                                        startActivity(intent);
                                    }
                                }).show();

                    } else {
                        ToastUtil.showLong(InqueryActivity.this, "咨询内容不能小于10个字符!");
                    }
                } else {
                    ToastUtil.showLong(InqueryActivity.this, "咨询内容不能为空!");
                }
                break;
        }
    }

    private void postInquery() {
        if (NetworkUtil.isNetworkAvailable(InqueryActivity.this)) {
            Map<String, RequestBody> maps = new HashMap<>();
            maps.put("content", DocUtils.toRequestBodyOfText(inqueryEditContent));
            maps.put("title", DocUtils.toRequestBodyOfText(desease));
            maps.put("receiveUserId", DocUtils.toRequestBodyOfText(doctorUserId));
            HttpRequestClient.getInstance(InqueryActivity.this).createBaseApi().uploads("upload/uploadPatient/", maps, new BaseObserver<ResponseBody>(InqueryActivity.this) {
                protected Disposable disposable;

                @Override
                public void onSubscribe(Disposable d) {
                    disposable = d;
                }

                @Override
                protected void onHandleSuccess(ResponseBody responseBody) {
                    //上传文件成功
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
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传成功!");
                            } else {
                                ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
                            }
                            dialog.dismiss();
                            Intent intent = new Intent(InqueryActivity.this, DoctorsListActivity.class);
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

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(RealDocApplication.getContext(), "病历信息上传失败!");
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
            });
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
