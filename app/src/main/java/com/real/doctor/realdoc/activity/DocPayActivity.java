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
    public String userId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_pay;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(DocPayActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询定价");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(DocPayActivity.this, Constants.USER_KEY, "");
        getPay();
    }

    private void getPay() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        HttpRequestClient.getInstance(DocPayActivity.this).createBaseApi().get("askQuestion/getAskQuestionMoney"
                , param, new BaseObserver<ResponseBody>(DocPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(DocPayActivity.this, "获取咨询定价信息失败!");
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
                        String msg = "";
                        String code = "";
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
                                    if (DocUtils.hasValue(obj, "chatMoney")) {
                                        String chatMoney = obj.getString("chatMoney");
                                        chatPay.setText(chatMoney);
                                        chatPay.setSelection(chatPay.getText().length());
                                    }
                                    if (DocUtils.hasValue(obj, "questionMoney")) {
                                        String questionMoney = obj.getString("questionMoney");
                                        recordPay.setText(questionMoney);
                                        recordPay.setSelection(recordPay.getText().length());
                                    }
                                } else {
                                    ToastUtil.showLong(DocPayActivity.this, "获取咨询定价信息失败!");
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
        if (Integer.valueOf(chatPayEdit) > 1000) {
            ToastUtil.showLong(DocPayActivity.this, "您定的价格大于1000，请重新输入!");
            return;
        }
        if (Integer.valueOf(recordPayEdit) > 1000) {
            ToastUtil.showLong(DocPayActivity.this, "您定的价格大于1000，请重新输入!");
            return;
        }
        if (EmptyUtils.isNotEmpty(chatPayEdit) || EmptyUtils.isNotEmpty(recordPayEdit)) {
            json = new JSONObject();
            try {
                json.put("userId", userId);
                json.put("chatMoney", chatPayEdit);
                json.put("questionMoney", recordPayEdit);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtil.showLong(DocPayActivity.this, "请输入聊天咨询,病历咨询收取的费用");
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(DocPayActivity.this).createBaseApi().json("askQuestion/setAskQuestionMoney/"
                , body, new BaseObserver<ResponseBody>(DocPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RealDocApplication.getContext(), "咨询费用提交失败!");
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
                                    ToastUtil.showLong(RealDocApplication.getContext(), "咨询费用提交成功!");
                                    finish();
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
