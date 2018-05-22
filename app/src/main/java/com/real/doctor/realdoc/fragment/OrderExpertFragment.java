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
import com.real.doctor.realdoc.model.DeptBean;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class OrderExpertFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @BindView(R.id.lv_expert)
    ListView listView;
    private Unbinder unbinder;
    ArrayList<ExpertBean> arrayList=new ArrayList<ExpertBean>();
    ExpertAdapter expertAdapter;

    public static OrderExpertFragment newInstance(String  hospitalId,String deptName) {
        OrderExpertFragment fragment=new OrderExpertFragment();
        Bundle bundel=new Bundle();
        bundel.putString("hospitalId",hospitalId);
        bundel.putString("deptName",deptName);
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
        if(getArguments()!=null) {
            String hospitalId =(String)getArguments().get("hospitalId");
            String deptName=(String)getArguments().get("deptName");
            expertAdapter=new ExpertAdapter(getContext(),arrayList);
            listView.setAdapter(expertAdapter);
            getExpert(hospitalId,deptName);
        }
    }

    private void getExpert(String hospitalId,String deptName){
        HashMap<String,Object> param=new HashMap<>();
        param.put("hospitalId",hospitalId);
        param.put("deptName",deptName);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get(" guahao/hospital/orderExpert/"
                , param, new BaseObserver<ResponseBody>(getContext()) {

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
                                    arrayList.addAll((ArrayList<ExpertBean>)localGson.fromJson(jsonObject.toString(),
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
        ProductBean bean =(ProductBean) parent.getAdapter().getItem(position);
        Intent intent =new Intent(getContext(),ProductShowActivity.class);
        intent.putExtra("model",bean);
        startActivity(intent);
    }
}
