package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.FragPagerAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.OrderExpertFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/23.
 */

public class OrderExpertActivity extends BaseActivity {
    @BindView(R.id.rg)
    RadioGroup radioGroup;
    @BindView(R.id.rb_expert)
    RadioButton radioButtonExpert;
    @BindView(R.id.rb_date)
    RadioButton radioButtonDate;
    @BindView(R.id.vp_show)
    ViewPager viewPager;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    public String hospitalId="1";
    public String deptName="呼吸内科";
    public FragPagerAdapter adapter;
    private static final int DEFAULT_OFFSCREEN_PAGES =2;
    @Override
    public int getLayoutId() {
        return R.layout.activity_order_expert;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
//        hospitalId=getIntent().getStringExtra("hospitalId");
//        deptName=getIntent().getStringExtra("deptName");
        page_title.setText("预约专家");
        ArrayList<Fragment> list=new ArrayList<Fragment>();
        OrderExpertFragment orderExpertFragment= OrderExpertFragment.newInstance(hospitalId,deptName);
        OrderExpertFragment orderExpertFragment2= OrderExpertFragment.newInstance(hospitalId,deptName);
        list.add(orderExpertFragment);
        list.add(orderExpertFragment2);
        adapter=new FragPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_expert:// first
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rb_date:// 第二个
                        viewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
}
