package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.HomeFragment;
import com.real.doctor.realdoc.fragment.MessageFragment;
import com.real.doctor.realdoc.fragment.ReadFragment;
import com.real.doctor.realdoc.fragment.ShoppintMallFragment;
import com.real.doctor.realdoc.fragment.UserFragment;
import com.real.doctor.realdoc.util.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RealDocActivity extends BaseActivity {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.homepage)
    RadioButton homepage;
    @BindView(R.id.read)
    RadioButton read;
    @BindView(R.id.message)
    RadioButton message;
    @BindView(R.id.user)
    RadioButton user;
    //fragments
    private ArrayList<Fragment> fragments;
    private HomeFragment homeFragment;
    private ReadFragment readFragment;
    private ShoppintMallFragment shoppintMallFragment;
    private UserFragment userFragment;
    private FragmentTransaction transaction;
    private Fragment tempFragment;
    private int position = 0;
    private long exitTime = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_real_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        fragments = new ArrayList<>();
        homeFragment = new HomeFragment();
        readFragment = new ReadFragment();
        shoppintMallFragment = ShoppintMallFragment.newInstance();
        userFragment = new UserFragment();

        fragments.add(homeFragment);
        fragments.add(readFragment);
        fragments.add(shoppintMallFragment);
        fragments.add(userFragment);
        tempFragment = homeFragment;
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.frame_layout, homeFragment).commit();
    }

    @Override
    public void initEvent() {
        radioGroup.check(R.id.homepage);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.homepage:
                        position = 0;
                        homeFragment.onShowMenu();
                        break;
                    case R.id.read:
                        position = 1;
                        homeFragment.onDestroyMenu();
                        break;
                    case R.id.message:
                        position = 2;
                        homeFragment.onDestroyMenu();
                        break;
                    case R.id.user:
                        position = 3;
                        homeFragment.onDestroyMenu();
                        break;
                    default:
                        position = 0;
                        break;
                }
                //根据位置得到相应的Fragment
                Fragment fragment = getFragment(position);
                switchFragment(tempFragment, fragment);
            }
        });
    }

    /**
     * 根据位置得到对应的 Fragment
     *
     * @param position
     * @return
     */
    private Fragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            Fragment baseFragment = fragments.get(position);
            return baseFragment;
        }
        return null;
    }

    /**
     * 切换Fragment
     *
     * @param fragment
     * @param nextFragment
     */
    private void switchFragment(Fragment fragment, Fragment nextFragment) {
        if (tempFragment != nextFragment) {
            tempFragment = nextFragment;
            if (nextFragment != null) {
                transaction = getSupportFragmentManager().beginTransaction();
                //判断nextFragment是否添加成功
                if (!nextFragment.isAdded()) {
                    //隐藏当前的Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    //添加Fragment
                    transaction.add(R.id.frame_layout, nextFragment).commit();
                } else {
                    //隐藏当前Fragment
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                    transaction.show(nextFragment).commit();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitApp();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showLong(this, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }
}
