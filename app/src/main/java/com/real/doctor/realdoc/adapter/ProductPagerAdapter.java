package com.real.doctor.realdoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.real.doctor.realdoc.fragment.ProductShowFragment;
import com.real.doctor.realdoc.model.CategoryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @user  lqm
 * @desc  ViewPager+Fragment 的适配器
 */

public class ProductPagerAdapter extends FragmentPagerAdapter {
    public ArrayList<CategoryBean> categoryBeanArrayList;
    public ProductPagerAdapter(FragmentManager fm, ArrayList<CategoryBean> categoryBeanArrayList) {
        super(fm);
        this.categoryBeanArrayList = categoryBeanArrayList;
    }

    @Override
    public Fragment getItem(int position) {
        return ProductShowFragment.newInstance(categoryBeanArrayList.get(position));
    }

    @Override
    public int getCount() {
        return categoryBeanArrayList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return categoryBeanArrayList.get(position).categoryName;
    }
}
