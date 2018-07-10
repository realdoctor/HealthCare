package com.real.doctor.realdoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.real.doctor.realdoc.activity.MyFollowNewsActivity;
import com.real.doctor.realdoc.fragment.ArticleShowFragment;
import com.real.doctor.realdoc.fragment.MyFollowNewsFragment;
import com.real.doctor.realdoc.fragment.ReadFragment;

/**
 * Created by ZFT on 2018/6/29.
 */

public class ArticleFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"发现","关注"};

    public ArticleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return MyFollowNewsFragment.newInstance();
        }
        return ReadFragment.newInstance();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}

