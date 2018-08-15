package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.adapter.ProductPagerAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ProductShowByCategoryActivity extends BaseActivity {
    @BindView(R.id.tb_category)
    TabLayout tb_category;
    @BindView(R.id.vp_show)
    ViewPager viewPager;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.home_search)
    RelativeLayout relativeLayout;
    @BindView(R.id.top_title)
    RelativeLayout topTitle;
    public ProductPagerAdapter productPagerAdapter;
    public ArrayList<CategoryBean> categoryBeanArrayList = new ArrayList<CategoryBean>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_products_category;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ProductShowByCategoryActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
            lp.topMargin = statusHeight;
            topTitle.setLayoutParams(lp);
        }
        virtulData();
        for (CategoryBean bean : categoryBeanArrayList) {
            tb_category.addTab(tb_category.newTab().setText(bean.categoryName).setText(bean.categoryId));
        }
        productPagerAdapter = new ProductPagerAdapter(getSupportFragmentManager(), categoryBeanArrayList);
        viewPager.setAdapter(productPagerAdapter);
        tb_category.setupWithViewPager(viewPager);
//        DocUtils.setIndicator(this, tb_category, 20, 20);
    }

    public void virtulData() {
        CategoryBean bean = new CategoryBean();
        bean.categoryId = "1";
        bean.categoryName = "医疗器械";
        CategoryBean bean2 = new CategoryBean();
        bean2.categoryId = "2";
        bean2.categoryName = "药品";
        categoryBeanArrayList.add(bean);
        categoryBeanArrayList.add(bean2);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.home_search})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                ProductShowByCategoryActivity.this.finish();
                break;
            case R.id.home_search:
                Intent intent = new Intent(ProductShowByCategoryActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
