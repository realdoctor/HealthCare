package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ArticleFragmentAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class NewDetailActivity extends BaseActivity {

    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.sc_id)
    ScrollView scrollView;
    @BindView(R.id.tv_autor)
    TextView tv_autor;
    @BindView(R.id.tv_profer)
    TextView tv_profer;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.tv_focus)
    TextView tv_focus;
    @BindView(R.id.new_detail)
    TextView new_detail;
    public String newsId;
    public String userId;
    private String focusFlag;
    public boolean flag = true;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_article_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(NewDetailActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        userId = (String) SPUtils.get(NewDetailActivity.this, Constants.USER_KEY, "");
        newsId = getIntent().getStringExtra("newsId");
        focusFlag = getIntent().getStringExtra("focusFlag");
        if (EmptyUtils.isNotEmpty(focusFlag)) {
            if (focusFlag.equals("0")) {
                tv_focus.setText("关注");
            } else if (focusFlag.equals("1")) {
                tv_focus.setText("取消关注");
            }
        } else {
            tv_focus.setText("取消关注");
        }
        getData();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.tv_focus})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                goBackBtn();
                break;
            case R.id.tv_focus:
                if (!flag) {
                    postFocus();
                } else {
                    postUnFocus();
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("newsId", newsId);
        HttpRequestClient.getNotInstance(NewDetailActivity.this, HttpNetUtil.BASE_URL, null).createBaseApi().get("healthnews/info"
                , params, new BaseObserver<ResponseBody>(NewDetailActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(NewDetailActivity.this, "获取咨询详情失败!");
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
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    NewModel model = (NewModel) localGson.fromJson(jsonObject.toString(), NewModel.class);
                                    page_title.setText(model.newsName);
                                    new_detail.setText(model.article);
                                    tv_autor.setText(model.newsAuthor);
                                    tv_profer.setText(model.authorProfer);
                                    tv_time.setText(DateUtil.timeStamp2Date(model.createDate, "yyyy年MM月dd日 HH:mm"));
                                    tv_type.setText(model.newsType);
                                } else {
                                    ToastUtil.showLong(NewDetailActivity.this, "获取咨询详情失败!");
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

    private void postFocus() {
        JSONObject json = new JSONObject();
        try {
            json.put("newsId", newsId);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getNotInstance(NewDetailActivity.this, HttpNetUtil.BASE_URL, null).createBaseApi().json("healthnews/focus/"
                , body, new BaseObserver<ResponseBody>(NewDetailActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    flag = true;
                                    tv_focus.setText("取消关注");
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "关注失败!");
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

    private void postUnFocus() {
        JSONObject json = new JSONObject();
        try {
            json.put("newsId", newsId);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getNotInstance(NewDetailActivity.this, HttpNetUtil.BASE_URL, null).createBaseApi().json("healthnews/focus/off/"
                , body, new BaseObserver<ResponseBody>(NewDetailActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    flag = false;
                                    tv_focus.setText("关注");
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "取消关注失败!");
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBackBtn();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBackBtn() {
        //点击回退按钮,广播通知刷新列表
        //动态注册广播
        Intent intent = new Intent(ArticleFragmentAdapter.REFRESH_DATA);
        LocalBroadcastManager.getInstance(NewDetailActivity.this).sendBroadcast(intent);
        finish();
    }
}
