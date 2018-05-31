package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.FragPagerAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.OrderExpertByDateFragment;
import com.real.doctor.realdoc.fragment.OrderExpertByNameFragment;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.view.CustomViewPager;

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
    CustomViewPager viewPager;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
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
        int statusHeight = ScreenUtil.getStatusHeight(OrderExpertActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        page_title.setText("预约专家");
        ArrayList<Fragment> list=new ArrayList<Fragment>();
        OrderExpertByNameFragment orderExpertFragment= OrderExpertByNameFragment.newInstance(hospitalId,deptName);
        OrderExpertByDateFragment orderExpertFragment2= OrderExpertByDateFragment.newInstance(hospitalId,deptName);
        list.add(orderExpertFragment);
        list.add(orderExpertFragment2);
        adapter=new FragPagerAdapter(getSupportFragmentManager(),list);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);

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
}
