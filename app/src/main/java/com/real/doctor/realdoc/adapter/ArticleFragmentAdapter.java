package com.real.doctor.realdoc.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;

import com.real.doctor.realdoc.activity.AccountActivity;
import com.real.doctor.realdoc.activity.MyFollowNewsActivity;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.fragment.ArticleShowFragment;
import com.real.doctor.realdoc.fragment.MyFollowNewsFragment;
import com.real.doctor.realdoc.fragment.ReadFragment;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.SPUtils;

/**
 * Created by ZFT on 2018/6/29.
 */

public class ArticleFragmentAdapter extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"发现", "关注"};
    private MyFollowNewsFragment myFollowNews;
    //监听刷新数据
    public static final String REFRESH_DATA = "android.intent.action.record.refresh.data";

    public ArticleFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            myFollowNews = MyFollowNewsFragment.newInstance();
            return myFollowNews;
        }
        localBroadcast();
        return ReadFragment.newInstance();
    }

    public void getFragmentData(String userId) {
        myFollowNews.getFragData(userId);
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

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(RealDocApplication.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REFRESH_DATA);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String userId = (String) SPUtils.get(RealDocApplication.getContext(), Constants.USER_KEY, "");
                getFragmentData(userId);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

}

