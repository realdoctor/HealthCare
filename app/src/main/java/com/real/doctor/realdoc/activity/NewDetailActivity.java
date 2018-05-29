package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class NewDetailActivity extends BaseActivity {
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.autor_name)
    TextView autor_name;
    @BindView(R.id.autor_profer)
    TextView autor_profer;
    @BindView(R.id.new_createtime)
    TextView new_createtime;
    @BindView(R.id.new_type)
    TextView new_type;
    @BindView(R.id.new_detail)
    TextView new_detail;
    public String newsId;
    @Override
    public int getLayoutId() {
        return R.layout.activity_new_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        newsId= getIntent().getStringExtra("newsId");
        getData();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                NewDetailActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    private void getData() {
        HashMap<String,Object> params=new HashMap<String,Object>();
        params.put("newsId",newsId);
        HttpRequestClient.getInstance(NewDetailActivity.this).createBaseApi().get("healthnews/info"
                , params, new BaseObserver<ResponseBody>(NewDetailActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONObject jsonObject=object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    NewModel  model=(NewModel)localGson.fromJson(jsonObject.toString(), NewModel.class);
                                   // page_title.setText(model.newsName);
                                    autor_name.setText(model.newsAuthor);
                                    autor_profer.setText(model.authorProfer);
                                    new_createtime.setText(model.createDate);
                                    new_type.setText(model.newsType);
                                    new_detail.setText(model.article);


                                } else {

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
