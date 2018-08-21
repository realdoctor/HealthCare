package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ExpertByDateAdapter;
import com.real.doctor.realdoc.adapter.OrderDateAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.WeekModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class OrderExpertByDateActivity extends BaseActivity implements ExpertByDateAdapter.MyClickListener {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.hlv_date)
    HorizontalListView horizontalListView;
    @BindView(R.id.lv_expert)
    ListView lv_expert;
    ArrayList<ExpertBean> arrayList = new ArrayList<ExpertBean>();
    ExpertByDateAdapter expertAdapter;
    ArrayList<WeekModel> weekList = new ArrayList<WeekModel>();
    OrderDateAdapter orderDateAdapter;
    private String hospitalId;
    private String deptName;
    private String doctorCode;
    private String userId;
    private Dialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_expert_by_date;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(OrderExpertByDateActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("预约专家");
        mProgressDialog = DocUtils.getProgressDialog(OrderExpertByDateActivity.this, "正在加载数据....");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(OrderExpertByDateActivity.this, Constants.USER_KEY, "");
        doctorCode = getIntent().getStringExtra("doctorCode");
        hospitalId = getIntent().getStringExtra("hospitalId");
        deptName = getIntent().getStringExtra("deptName");
        expertAdapter = new ExpertByDateAdapter(OrderExpertByDateActivity.this, arrayList, this);
        lv_expert.setAdapter(expertAdapter);
        orderDateAdapter = new OrderDateAdapter(OrderExpertByDateActivity.this, weekList);
        horizontalListView.setAdapter(orderDateAdapter);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int location = position;
                orderDateAdapter.setSelectedPosition(position);
                orderDateAdapter.notifyDataSetInvalidated();
                WeekModel model = (WeekModel) parent.getAdapter().getItem(position);
                getExpert(hospitalId, deptName, model.worktimeWeek.split("\\|")[0]);
            }
        });
        getExpertDate(hospitalId, deptName);
    }

    @Override
    public void initEvent() {

    }


    @Override
    public void doBusiness(Context mContext) {

    }

    private void getExpert(String hospitalId, String deptName, String orderDay) {
        mProgressDialog.show();
        HashMap<String, Object> param = new HashMap<>();
        param.put("doctorCode", doctorCode);
        param.put("hospitalId", hospitalId);
        param.put("deptName", deptName);
        param.put("orderDay", orderDay);
        HttpRequestClient.getInstance(OrderExpertByDateActivity.this).createBaseApi().get(" guahao/hospital/orderDateExpert/"
                , param, new BaseObserver<ResponseBody>(OrderExpertByDateActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(OrderExpertByDateActivity.this, "获取专家列表失败!");
                        mProgressDialog.dismiss();
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
                                    arrayList.clear();
                                    JSONArray jsonObject = object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    arrayList.addAll((ArrayList<ExpertBean>) localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<ExpertBean>>() {
                                            }.getType()));
                                    expertAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.showLong(OrderExpertByDateActivity.this, "获取专家列表失败!");
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

    private void getExpertDate(String hospitalId, String deptName) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("hospitalId", hospitalId);
        param.put("deptName", deptName);
        param.put("doctorCode", doctorCode);
        HttpRequestClient.getInstance(OrderExpertByDateActivity.this).createBaseApi().get(" guahao/hospital/orderDate/"
                , param, new BaseObserver<ResponseBody>(OrderExpertByDateActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONArray jsonObject = object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    weekList.addAll((ArrayList<WeekModel>) localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<WeekModel>>() {
                                            }.getType()));
                                    if (weekList.size() > 0) {
                                        orderDateAdapter.notifyDataSetChanged();
                                        selectDefault();
                                    } else {
                                        ToastUtil.showLong(OrderExpertByDateActivity.this,"该医生无法预约!");
                                        finish();
                                    }
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

    //默认选中
    private void selectDefault() {
        final int location = 0;
        orderDateAdapter.setSelectedPosition(0);
        orderDateAdapter.notifyDataSetInvalidated();
        final WeekModel bean = (WeekModel) orderDateAdapter.getItem(0);
        String orderDate = bean.worktimeWeek.split("\\|")[0];
        getExpert(hospitalId, deptName, orderDate);
    }

    @Override
    public void clickListener(View v) {
        ExpertBean bean = (ExpertBean) v.getTag();
        orderExpert(bean);
    }

    public void orderExpert(ExpertBean bean) {
        JSONObject object = new JSONObject();
        try {
            object.put("deptId", bean.deptId);
            object.put("doctorCode", bean.doctorCode);
            object.put("hospitalDoctorDutyId", bean.hospitalDoctorDutyId);
            object.put("hospitalId", bean.hospitalId);
            object.put("orderDay", bean.dutyDtime);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(OrderExpertByDateActivity.this, Constants.TOKEN, "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(OrderExpertByDateActivity.this, "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client = HttpRequestClient.getInstance(OrderExpertByDateActivity.this, HttpNetUtil.BASE_URL, header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("guahao/fastorder/"
                , body, new BaseObserver<ResponseBody>(OrderExpertByDateActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    ToastUtil.showLong(OrderExpertByDateActivity.this, "预约成功!");
                                } else {
                                    ToastUtil.showLong(OrderExpertByDateActivity.this, "预约失败!");
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
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }
}
