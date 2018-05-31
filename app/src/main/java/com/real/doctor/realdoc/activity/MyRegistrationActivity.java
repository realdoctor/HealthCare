package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.RegistrationAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.RegistrationModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class MyRegistrationActivity extends BaseActivity {
    @BindView(R.id.lv_registration)
    ListView lv_registration;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    RegistrationAdapter registrationAdapter;
    ArrayList<RegistrationModel> registrationModelArrayList=new ArrayList<RegistrationModel>();
    public String userid;
    @Override
    public int getLayoutId() {
        return R.layout.activity_my_registration;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyRegistrationActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        page_title.setText("我的预约");
        userid= (String)SPUtils.get(MyRegistrationActivity.this, Constants.USER_KEY,"");
        registrationAdapter=new RegistrationAdapter(MyRegistrationActivity.this,registrationModelArrayList);
        lv_registration.setAdapter(registrationAdapter);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                MyRegistrationActivity.this.finish();
                break;
        }

    }

    @Override
    public void doBusiness(Context mContext) {
        getData();
    }
    private void getData(){
        HashMap<String,Object> param=new HashMap<>();
        param.put("userid",userid);
        HttpRequestClient.getInstance(MyRegistrationActivity.this).createBaseApi().get(" user/myGuahaoOrder/"
                , param, new BaseObserver<ResponseBody>(MyRegistrationActivity.this) {

                    @Override

                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONArray jsonObject=object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    registrationModelArrayList.addAll((ArrayList<RegistrationModel>)localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<RegistrationModel>>() {
                                            }.getType()));
                                    registrationAdapter.notifyDataSetChanged();

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
