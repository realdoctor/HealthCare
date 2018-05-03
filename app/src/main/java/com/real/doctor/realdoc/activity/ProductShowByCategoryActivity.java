package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.adapter.ProductPagerAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.model.ProductInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ProductShowByCategoryActivity extends BaseActivity {
    @BindView(R.id.tb_category)
    TabLayout tb_category;
    @BindView(R.id.vp_show)
    ViewPager viewPager;
    public ProductPagerAdapter productPagerAdapter;
    public ArrayList<CategoryBean> categoryBeanArrayList=new ArrayList<CategoryBean>();

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
        virtulData();
        for(CategoryBean bean:categoryBeanArrayList){
            tb_category.addTab(tb_category.newTab().setText(bean.categoryName).setText(bean.categoryId));
        }
        productPagerAdapter=new ProductPagerAdapter(getSupportFragmentManager(),categoryBeanArrayList);
        viewPager.setAdapter(productPagerAdapter);
        tb_category.setupWithViewPager(viewPager);
    }

    public void virtulData(){
        for(int i=0;i<10;i++){
            CategoryBean bean=new CategoryBean();
            bean.categoryId=i+"d";
            bean.categoryName=i+"name";
            for(int k=0;k<4;k++){
                BrandBean brandBean=new BrandBean();
                brandBean.breadId=i+"k"+k;
                brandBean.breadName=i+"k"+k;
                for(int l=0;l<10;l++){
                    ProductInfo productInfo=new ProductInfo("l"+l+i+"i","name"+l+"!"+i+"!"+k,"http://img.zcool.cn/community/0166c756e1427432f875520f7cc838.jpg","",88d,22);
                    brandBean.productList.add(productInfo);
                }
                bean.brands.add(brandBean);
            }
            categoryBeanArrayList.add(bean);
        }
    }
    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
