package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class SearchProductResultActivity extends BaseActivity implements OnLoadmoreListener,OnRefreshListener,AdapterView.OnItemClickListener,ProductAdapter.ClickListener {
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.lv_products)
    ListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.page_title)
    TextView page_title;
    public int pageNum=1;
    public int pageSize=10;
    public String userId;
    public ProductAdapter productAdapter;
    private PageModel<ProductBean> baseModel = new PageModel<ProductBean>();
    public ArrayList<ProductBean> productList=new ArrayList<ProductBean>();
    public String searchKey;
    public String categoryId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_product;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SearchProductResultActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        userId= (String)SPUtils.get(SearchProductResultActivity.this, Constants.USER_KEY,"");
        searchKey=getIntent().getStringExtra("searchKey");
        categoryId=getIntent().getStringExtra("categoryId");
        page_title.setText("搜索结果");
        ClassicsHeader mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
        ClassicsFooter footer=(ClassicsFooter) refreshLayout.getRefreshFooter();
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        productAdapter = new ProductAdapter(SearchProductResultActivity.this, productList);
        productAdapter.setmListener(this);
        listView.setAdapter(productAdapter);
        listView.setOnItemClickListener(this);
        getRefreshProducts();


    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                SearchProductResultActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    public  void getRefreshProducts(){
        HashMap<String,Object> param=new HashMap<>();
        param.put("categoryId",categoryId);
        param.put("pageNum",pageNum);
        param.put("pageSize",pageSize);
        param.put("searchstr",searchKey);
        HttpRequestClient.getInstance(SearchProductResultActivity.this).createBaseApi().get(" goods/"
                , param, new BaseObserver<ResponseBody>(SearchProductResultActivity.this) {

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
                                            new TypeToken<PageModel<ProductBean>>() {
                                            }.getType());
                                    productList.addAll(baseModel.list);
                                    productAdapter.notifyDataSetChanged();
                                    refreshLayout.finishRefresh();
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
    public void onLoadmore(RefreshLayout refreshlayout) {
        if(pageSize*pageNum>productList.size()){
            ToastUtil.show(SearchProductResultActivity.this,"已经是最后一页",Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        getRefreshProducts();
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum=1;
        productList.clear();
        getRefreshProducts();
        refreshLayout.finishRefresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProductBean bean =(ProductBean) parent.getAdapter().getItem(position);
        Intent intent =new Intent(SearchProductResultActivity.this, ProductShowActivity.class);
        intent.putExtra("model",bean);
        startActivity(intent);
    }

    @Override
    public void clickListener(View v) {
        if(userId==null||userId.length()==0){
            Intent intent=new Intent(SearchProductResultActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            ProductBean bean=(ProductBean) v.getTag();
            addToCart(bean.getGoodsId(),1);
        }

    }
    //添加到购物车
    public void addToCart(String goodsId,int num){
        JSONObject object=new JSONObject();
        try {
            object.put("goodsId",goodsId);
            object.put("num",num);
            object.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestClient client= HttpRequestClient.getInstance(SearchProductResultActivity.this, HttpNetUtil.BASE_URL);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("cart/addCartItem/"
                , body, new BaseObserver<ResponseBody>(SearchProductResultActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
                        ToastUtil.showLong(SearchProductResultActivity.this, "加入购物车失败!");
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
                                    ToastUtil.showLong(SearchProductResultActivity.this, "加入购物车成功!");
                                } else {
                                    ToastUtil.showLong(SearchProductResultActivity.this, "加入购物车失败!");
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
    protected void onResume() {
        super.onResume();
        userId= (String)SPUtils.get(SearchProductResultActivity.this, Constants.USER_KEY,"");
    }
}
