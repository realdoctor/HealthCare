package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ProductShowActivity;
import com.real.doctor.realdoc.adapter.ExpertAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class OrderExpertByNameFragment extends BaseFragment implements AdapterView.OnItemClickListener, ExpertAdapter.MyClickListener {
    @BindView(R.id.lv_expert)
    ListView listView;
    private Unbinder unbinder;
    ArrayList<ExpertBean> arrayList = new ArrayList<ExpertBean>();
    ExpertAdapter expertAdapter;
    public String userId;

    public static OrderExpertByNameFragment newInstance(String hospitalId, String deptName) {
        OrderExpertByNameFragment fragment = new OrderExpertByNameFragment();
        Bundle bundel = new Bundle();
        bundel.putString("hospitalId", hospitalId);
        bundel.putString("deptName", deptName);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order_expert_item;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(final Context mContext) {
        if (getArguments() != null) {
            userId = (String) SPUtils.get(getContext(), Constants.USER_KEY, "");
            String hospitalId = (String) getArguments().get("hospitalId");
            String deptName = (String) getArguments().get("deptName");
            expertAdapter = new ExpertAdapter(getContext(), arrayList, this);
            listView.setAdapter(expertAdapter);
            getExpert(hospitalId, deptName);
        }
    }

    private void getExpert(String hospitalId, String deptName) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("hospitalId", hospitalId);
        param.put("deptName", deptName);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get(" guahao/hospital/orderExpert/"
                , param, new BaseObserver<ResponseBody>(getContext()) {
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
                                    arrayList.addAll((ArrayList<ExpertBean>) localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<ExpertBean>>() {
                                            }.getType()));
                                    expertAdapter.notifyDataSetChanged();

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

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProductBean bean = (ProductBean) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getContext(), ProductShowActivity.class);
        intent.putExtra("model", bean);
        startActivity(intent);
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
        String token = (String) SPUtils.get(getContext(), Constants.TOKEN, "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(getContext(), "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client = HttpRequestClient.getInstance(getContext(), HttpNetUtil.BASE_URL, header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("guahao/fastorder/"
                , body, new BaseObserver<ResponseBody>(getContext()) {
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
                                    ToastUtil.showLong(getContext(), "预约成功!");
                                } else {
                                    ToastUtil.showLong(getContext(), "预约失败!");
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
