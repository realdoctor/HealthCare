package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.AlreadyRevisitFragment;
import com.real.doctor.realdoc.fragment.RevisitingFragment;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.widget.TabViewPagerAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyRevisitActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private String doctorUserId;
    private TabViewPagerAdapter viewPagerAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"正在复诊中", "复诊已完毕"};
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_revisit;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyRevisitActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("我的复诊");
    }

    @Override
    public void initData() {
        //设置TabLayout标签的显示方式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        //设置TabLayout点击事件
        tabLayout.setOnTabSelectedListener(this);
        fragments.add(new RevisitingFragment());
        fragments.add(new AlreadyRevisitFragment());
        viewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        DocUtils.setIndicator(this, tabLayout, 20, 20);
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.layout_divider_vertical));
        linearLayout.setDividerPadding(SizeUtils.dp2px(this, 15));
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            doctorUserId = intent.getExtras().getString("doctorUserId");
        }
    }

    @Override
    public void initEvent() {

    }


    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                goBackBtn();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBackBtn();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBackBtn() {
        if (EmptyUtils.isNotEmpty(doctorUserId)) {
            Intent intent = new Intent(MyRevisitActivity.this, DoctorsListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            finish();
        }
    }
}
