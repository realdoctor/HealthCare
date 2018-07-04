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
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.InqueryBean;
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
                String inqueryEditContent = inqueryEdit.getText().toString();
                if (EmptyUtils.isNotEmpty(inqueryEditContent)) {
                    if (inqueryEditContent.length() > 10) {
                        //将问题传输给服务器端
//                        postInquery();
                        //进入病历列表页面
                        Intent intent = new Intent(InqueryActivity.this, CheckDocActivity.class);
                        intent.putExtra("inquery", inqueryEditContent);
                        startActivity(intent);
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
        JSONObject json = new JSONObject();
        try {
            json.put("inquery", "inquery");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(InqueryActivity.this).createBaseApi().json("user/login/"
                , body, new BaseObserver<ResponseBody>(InqueryActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(InqueryActivity.this, "咨询内容提交失败!");
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

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
                                    //跳转到病历打包页面
                                    ToastUtil.showLong(InqueryActivity.this, "咨询内容提交成功!");
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
