package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DoctorsAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class DoctorsListActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.doctors_list_rv)
    RecyclerView doctorsRv;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    DoctorsAdapter doctorsAdapter;
    List<DoctorBean> doctors;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doctors_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(DoctorsListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("在线复诊");
        rightIcon.setVisibility(View.GONE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            rightIcon.setBackground(getResources().getDrawable(R.mipmap.map_icon, null));
//        }
    }

    public void initData() {
        doctors = new ArrayList<>();
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        doctorsRv.addItemDecoration(divider);
        doctorsRv.setLayoutManager(new LinearLayoutManager(this));
        doctorsAdapter = new DoctorsAdapter(DoctorsListActivity.this, R.layout.doctors_list_item_view, doctors);
        doctorsRv.setAdapter(doctorsAdapter);
        //获得医生数据
        getDoctorsData("1");
    }

    private void getDoctorsData(String pageNum) {
        Map<String, String> map = new HashMap<String, String>();
        String mobile = (String) SPUtils.get(this, "mobile", "");
        map.put("mobilePhone", mobile);
        map.put("pageNum", pageNum);
        HttpRequestClient.getInstance(DoctorsListActivity.this).createBaseApi().get("patient/revisit"
                , map, new BaseObserver<ResponseBody>(DoctorsListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(DoctorsListActivity.this, "获取医生列表失败!");
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
                                    if (DocUtils.hasValue(obj, "list")) {
                                        doctors = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), DoctorBean.class);
                                        if (doctors.size() > 0) {
                                            doctorsAdapter = new DoctorsAdapter(DoctorsListActivity.this, R.layout.doctors_list_item_view, doctors);
                                            doctorsRv.setAdapter(doctorsAdapter);
                                            initEvent();
//                                            doctorsAdapter.notifyDataSetChanged();
                                        } else {
                                            ToastUtil.showLong(DoctorsListActivity.this, "医生列表为空!");
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(DoctorsListActivity.this, msg);
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
        doctorsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //跳转进医生详情
            }
        });
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


}
