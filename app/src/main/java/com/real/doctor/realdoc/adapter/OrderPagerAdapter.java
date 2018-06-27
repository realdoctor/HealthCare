package com.real.doctor.realdoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.real.doctor.realdoc.fragment.OrderFragment;
import com.real.doctor.realdoc.model.OrderStatusModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @user  lqm
 * @desc  ViewPager+Fragment 的适配器
 */

public class OrderPagerAdapter extends FragmentPagerAdapter {
    public ArrayList<OrderStatusModel> list=new ArrayList<OrderStatusModel>();
    public OrderPagerAdapter(FragmentManager fm, ArrayList<OrderStatusModel> list) {
        super(fm);
        this.list=list;
    }

    @Override
    public Fragment getItem(int position) {
        return OrderFragment.newInstance(list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }
    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).order_desc;
    }

}
