package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ProductItemAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.refreshrecyclerview.base.adapter.BaseRecyclerViewAdapter;
import com.real.doctor.realdoc.widget.refreshrecyclerview.pulltorefresh.PullToRefreshRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ProductListActivity extends BaseActivity implements PullToRefreshRecyclerView.OnRefreshAndLoadMoreListener,BaseRecyclerViewAdapter.OnItemClickListener{
    public ArrayList<ProductBean> productList=new ArrayList<ProductBean>();
    public ProductItemAdapter productAdapter;
    public int currentPage=1;
    @BindView(R.id.recycler_product_list)
    PullToRefreshRecyclerView mRecyclerView;
    @Override
    public int getLayoutId() {
        return R.layout.activity_product_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }
    @Override
    public void initData() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductListActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.setLoadMoreEnabled(true);
        mRecyclerView.setRefreshAndLoadMoreListener(this);
        getData();
    }

    @Override
    public void initEvent() {

    }

    public void refreshUI() {
        if (productAdapter == null) {
            productAdapter=new ProductItemAdapter(ProductListActivity.this,productList,null);
            productAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(productAdapter);
        } else {
            if (mRecyclerView != null) {
                if (mRecyclerView.isLoading()) {
                    mRecyclerView.loadMoreComplete();
                } else if (mRecyclerView.isRefreshing()) {
                    mRecyclerView.refreshComplete();
                }
            }
        }
    }

    @Override
    public void widgetClick(View v) {
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    public void getData(){
        for(int i=0;i<10;i++){
            ProductBean bean=new ProductBean();
            bean.product_detail="不错的产品";
            bean.product_price=200.00d;
            bean.product_name="产品名称"+i;
            bean.product_show_pic_url="http://img07.tooopen.com/images/20170316/tooopen_sy_201956178977.jpg";
            for(int k=0;k<2;k++){
                bean.imgs.add("http://img.zcool.cn/community/0166c756e1427432f875520f7cc838.jpg");
                bean.imgs.add("http://img.zcool.cn/community/018fdb56e1428632f875520f7b67cb.jpg");
            }
            productList.add(bean);
        }
        refreshUI();
    }

    @Override
    public void onRecyclerViewRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                productList.clear();
                getData();
            }
        }, 3000);
    }

    @Override
    public void onRecyclerViewLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 3000);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent =new Intent(ProductListActivity.this,ProductShowActivity.class);
        intent.putExtra("model",productList.get(position));
        startActivity(intent);
        finish();
    }
}
