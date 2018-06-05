package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ProductShowActivity;
import com.real.doctor.realdoc.adapter.BrandAdapter;
import com.real.doctor.realdoc.adapter.OrderShowAdapter;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.OrderModel;
import com.real.doctor.realdoc.model.OrderStatusModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.DynamicTimeFormat;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class OrderFragment extends BaseFragment implements OnLoadmoreListener,OnRefreshListener {
    private Unbinder unbinder;
    public OrderStatusModel bean;
    @BindView(R.id.lv_orders)
    ExpandableListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    public OrderShowAdapter orderAdapter;
    private ClassicsHeader mClassicsHeader;
    public String  userId;
    public int pageNum=1;
    public int pageSize=10;
    public boolean isFirstEnter=true;
    private PageModel<OrderModel> baseModel = new PageModel<OrderModel>();
    public ArrayList<OrderModel> orderModels =new ArrayList<OrderModel>();
    public static OrderFragment newInstance(OrderStatusModel bean) {
        OrderFragment fragment=new OrderFragment();
        Bundle bundel=new Bundle();
        bundel.putSerializable("model",bean);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_order;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(final Context mContext) {
        if(getArguments()!=null) {
            userId= (String)SPUtils.get(getContext(), Constants.USER_KEY,"");
            bean=(OrderStatusModel) getArguments().get("model");
            mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
            ClassicsFooter footer=(ClassicsFooter) refreshLayout.getRefreshFooter();
            refreshLayout.setOnLoadmoreListener(this);
            refreshLayout.setOnRefreshListener(this);
            orderAdapter = new OrderShowAdapter(mContext, orderModels);
            listView.setAdapter(orderAdapter);
            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    String name=orderModels.get(groupPosition).orderList.get(childPosition).goodsPrice;
                    return false;
                }
            });
            listView.setGroupIndicator(null);
            getRefreshOrders();

        }
    }

    private void getRefreshOrders(){
        HashMap<String,Object> param=new HashMap<>();
        param.put("tradeStatus",bean.orderstatus);
        param.put("pageNum",pageNum);
        param.put("pageSize",pageSize);
        param.put("userId",userId);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get(" goods/order/orderList/"
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
                                    JSONObject jsonObject=object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<OrderModel>>() {
                                            }.getType());
                                    orderModels.addAll(baseModel.list);
                                    orderAdapter.notifyDataSetChanged();
                                    int count=orderAdapter.getGroupCount();
                                    for (int i=0; i<count; i++) {
                                        listView.expandGroup(i);
                                    };
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
    public void onLoadmore(RefreshLayout refreshlayout) {
            if(pageSize*pageNum> orderModels.size()){
                ToastUtil.show(getContext(),"已经是最后一页",Toast.LENGTH_SHORT);
                refreshlayout.finishLoadmore();
                return;
            }
            pageNum++;
            getRefreshOrders();
            refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
            pageNum=1;
            orderModels.clear();
            getRefreshOrders();
            refreshlayout.finishRefresh();
    }


}
