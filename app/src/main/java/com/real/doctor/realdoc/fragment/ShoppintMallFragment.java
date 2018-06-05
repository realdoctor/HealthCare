package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ProductShowByCategoryActivity;
import com.real.doctor.realdoc.activity.SearchActivity;
import com.real.doctor.realdoc.activity.SearchHistoryListActivity;
import com.real.doctor.realdoc.adapter.ProductPagerAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ShoppintMallFragment extends BaseFragment {
    private Unbinder unbinder;
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
    public final static int SHOPPING_EVENT_REQUEST_CODE = 3;
    public ProductPagerAdapter productPagerAdapter;
    public ArrayList<CategoryBean> categoryBeanArrayList=new ArrayList<CategoryBean>();
    public static ShoppintMallFragment newInstance() {
        return new ShoppintMallFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_products_category;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
            lp.topMargin = statusHeight;
            topTitle.setLayoutParams(lp);
        }
        virtulData();
        for(CategoryBean bean:categoryBeanArrayList){
            tb_category.addTab(tb_category.newTab().setText(bean.categoryName).setText(bean.categoryId));
        }
        productPagerAdapter=new ProductPagerAdapter(getActivity().getSupportFragmentManager(),categoryBeanArrayList);
        viewPager.setAdapter(productPagerAdapter);
        tb_category.setupWithViewPager(viewPager);
    }
    public void virtulData(){
        CategoryBean bean=new CategoryBean();
        bean.categoryId="1";
        bean.categoryName="医疗器械";
        CategoryBean bean2=new CategoryBean();
        bean2.categoryId="2";
        bean2.categoryName="药品";
        categoryBeanArrayList.add(bean);
        categoryBeanArrayList.add(bean2);
    }
    @Override
    @OnClick(R.id.home_search)
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.home_search:
                Intent intent =new Intent(getContext(),SearchHistoryListActivity.class);
                intent.putExtra("requestCode",SHOPPING_EVENT_REQUEST_CODE);
                startActivityForResult(intent,SHOPPING_EVENT_REQUEST_CODE);
                break;


        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            String searchKey = data.getStringExtra("searchKey");

            if(requestCode == SHOPPING_EVENT_REQUEST_CODE) {
                ProductShowFragment fragment=productPagerAdapter.currentFragment;
                if(fragment!=null){
                    fragment.searchKey=searchKey;
                    fragment.pageNum=1;
                    fragment.getRefreshProducts();
                }
            }
        }
    }
}
