package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.MyPayAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.MyPayBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class MyPayActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.pay_in)
    TextView payIn;
    @BindView(R.id.pay_out)
    TextView payOut;
    @BindView(R.id.my_pay_recycler)
    RecyclerView myPayRecycler;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private int mPageNum = 1;
    MyPayAdapter myPayAdapter;
    private List<MyPayBean> myPayBeanList;
    private Dialog mProgressDialog;
    private String userId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_pay;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        mProgressDialog = DocUtils.getProgressDialog(MyPayActivity.this, "正在加载数据....");
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyPayActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("支付订单");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(MyPayActivity.this, Constants.USER_KEY, "");
        myPayBeanList = new ArrayList<>();
        getMyPayData("1");
        swipeRefresh();
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

    private void getMyPayData(String pageNum) {
        mProgressDialog.show();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("pageNum", pageNum);
        param.put("pageSize", "10");
        param.put("mark", "2");
        param.put("userId", userId);
        HttpRequestClient.getInstance(MyPayActivity.this).createBaseApi().get("account/payment/list"
                , param, new BaseObserver<ResponseBody>(MyPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPageNum == 1) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                        ToastUtil.showLong(MyPayActivity.this, "获取支付订单列表失败!");
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
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "total")) {
                                        String total = obj.getString("total");
                                        payOut.setText("支付 " + total);
                                    }
                                    if (DocUtils.hasValue(obj, "list")) {
                                        myPayBeanList = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), MyPayBean.class);
                                        if (myPayBeanList.size() > 0) {
                                            if (EmptyUtils.isEmpty(myPayAdapter)) {
                                                myPayAdapter = new MyPayAdapter(R.layout.my_pay_item, myPayBeanList);
                                                myPayRecycler.setLayoutManager(new LinearLayoutManager(MyPayActivity.this, LinearLayoutManager.VERTICAL, false));
                                                //添加Android自带的分割线
                                                myPayRecycler.addItemDecoration(new DividerItemDecoration(MyPayActivity.this, DividerItemDecoration.VERTICAL));
                                                myPayRecycler.setAdapter(myPayAdapter);
                                            } else {
                                                myPayAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(MyPayActivity.this, "获取支付订单失败!");
                                }
                                if (mPageNum == 1) {
                                    refreshLayout.finishRefresh();
                                } else {
                                    refreshLayout.finishRefresh();
                                    refreshLayout.finishLoadmore();
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

    private void swipeRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                //处理刷新列表逻辑
                getMyPayData("1");
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getMyPayData(String.valueOf(++mPageNum));
            }
        });
    }
}
