package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DoctorBean;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AnswerActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.inquery_info)
    TextView inqueryInfo;
    @BindView(R.id.inquery)
    TextView inquery;
    @BindView(R.id.answer_info)
    TextView answerInfo;
    @BindView(R.id.answer)
    TextView answer;
    private DoctorBean doctorBean;
    private String userId;
    private String questionId;
    private String retryNum;

    @Override
    public int getLayoutId() {
        return R.layout.activity_answer;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AnswerActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询解答");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(AnswerActivity.this, Constants.USER_KEY, "");
        Intent intent = getIntent();
        String inqueryText = "";
        String answerText = "";
        String doctorText = "";
        if (intent != null) {
            inqueryText = intent.getStringExtra("inquery");
            answerText = intent.getStringExtra("answer");
            doctorText = intent.getStringExtra("doctor");
            questionId = intent.getStringExtra("questionId");
            retryNum = intent.getStringExtra("retryNum");
        }
        inqueryInfo.setText("您咨询的问题:");
        inquery.setText(inqueryText);
        answerInfo.setText("医生" + doctorText + "的解答:");
        if (answerText.equals("")) {
            answer.setText("该问题医生还未解答!");
        } else {
            answer.setText(answerText);
        }
        postBackTime();
    }

    @Override
    public void initEvent() {

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

    private void postBackTime() {
        JSONObject json = new JSONObject();
        try {
            json.put("retryNum", retryNum);
            json.put("questionId", questionId);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(AnswerActivity.this).createBaseApi().json("askQuestion/reply/"
                , body, new BaseObserver<ResponseBody>(AnswerActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //do nothing 通知后台开始计时失败
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
                                    //do nothing 通知后台开始计时成功
                                } else {
                                    //do nothing 通知后台开始计时失败
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
