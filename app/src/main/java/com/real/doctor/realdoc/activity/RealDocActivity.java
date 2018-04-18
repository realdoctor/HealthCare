package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.HomeFragment;
import com.real.doctor.realdoc.fragment.ReadFragment;
import com.real.doctor.realdoc.fragment.MessageFragment;
import com.real.doctor.realdoc.fragment.UserFragment;
import com.real.doctor.realdoc.adapter.FragPagerAdapter;
import com.real.doctor.realdoc.util.UIUtils;
import com.real.doctor.realdoc.widget.IconFontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RealDocActivity extends BaseActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.if_home)
    IconFontTextView ifHome;
    @BindView(R.id.tv_home)
    TextView tvHome;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.if_read)
    IconFontTextView ifRead;
    @BindView(R.id.tv_read)
    TextView tvRead;
    @BindView(R.id.ll_read)
    LinearLayout llRead;
    @BindView(R.id.if_msg)
    IconFontTextView ifMsg;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.ll_msg)
    LinearLayout llMsg;
    @BindView(R.id.if_user)
    IconFontTextView ifUser;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.ll_user)
    LinearLayout llUser;

    private List<Fragment> mFragments = new ArrayList<>();

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
        setTabColor(ifHome, tvHome);
        mFragments.add(HomeFragment.newInstance());
        mFragments.add(ReadFragment.newInstance());
        mFragments.add(MessageFragment.newInstance());
        mFragments.add(UserFragment.newInstance());
        viewPager.setAdapter(new FragPagerAdapter(getSupportFragmentManager(), mFragments));
        viewPager.setCurrentItem(0, false);
        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTabColor(ifHome, tvHome);
                        break;
                    case 1:
                        setTabColor(ifRead, tvRead);
                        break;
                    case 2:
                        setTabColor(ifMsg, tvMsg);
                        break;
                    case 3:
                        setTabColor(ifUser, tvUser);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.ll_home,R.id.ll_read, R.id.ll_msg,R.id.ll_user})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                viewPager.setCurrentItem(0);
                setTabColor(ifHome, tvHome);
                break;
            case R.id.ll_read:
                viewPager.setCurrentItem(1);
                setTabColor(ifRead, tvRead);
                break;
            case R.id.ll_msg:
                viewPager.setCurrentItem(2);
                setTabColor(ifMsg, tvMsg);
                break;
            case R.id.ll_user:
                viewPager.setCurrentItem(3);
                setTabColor(ifUser, tvUser);
                break;

        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    private void setTabColor(IconFontTextView icon, TextView textView) {
        ifHome.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        tvHome.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        ifRead.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        tvRead.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        ifMsg.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        tvMsg.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        ifUser.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        tvUser.setTextColor(UIUtils.getColor(R.color.tab_nor_color));
        icon.setTextColor(UIUtils.getColor(R.color.tab_sel_color));
        textView.setTextColor(UIUtils.getColor(R.color.tab_sel_color));
    }
}
