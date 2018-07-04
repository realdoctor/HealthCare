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

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
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

public class DocPayActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.chat_pay)
    EditText chatPay;
    @BindView(R.id.record_pay)
    EditText recordPay;
    @BindView(R.id.confirm)
    Button confirm;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_pay;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(DocPayActivity .this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询定价");
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.confirm, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.confirm:
                confirmPay();
                break;
        }
    }

    private void confirmPay() {
        JSONObject json = null;
        String chatPayEdit = chatPay.getText().toString().trim();
        String recordPayEdit = recordPay.getText().toString().trim();
        if (EmptyUtils.isNotEmpty(chatPayEdit) || EmptyUtils.isNotEmpty(recordPayEdit)) {
            json = new JSONObject();
            try {
                json.put("chatPay", chatPayEdit);
                json.put("recordPay", recordPay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showLong(DocPayActivity.this, "请输入聊天咨询,病历咨询收取的费用");
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(DocPayActivity.this).createBaseApi().json("user/login/"
                , body, new BaseObserver<ResponseBody>(DocPayActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(DocPayActivity.this, e.getMessage());
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
                                    ToastUtil.showLong(RealDocApplication.getContext(), "咨询费用提交成功!");
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "咨询费用提交失败!");
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
