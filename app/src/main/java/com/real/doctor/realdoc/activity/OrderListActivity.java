package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.OrderPagerAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.OrderStatusModel;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/23.
 */

public class OrderListActivity extends BaseActivity {
    @BindView(R.id.tb_order_statues)
    TabLayout tableLayout;
    @BindView(R.id.vp_show)
    ViewPager viewPager;
    @BindView(R.id.title_bar)
    RelativeLayout title_bar;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.finish_back)
    ImageView back;
    OrderPagerAdapter orderPagerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(OrderListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) title_bar.getLayoutParams();
            lp.topMargin = statusHeight;
            title_bar.setLayoutParams(lp);
        }
        page_title.setText("我的订单");
        for(OrderStatusModel model: DataUtil.orderStatusModels){
            tableLayout.addTab(tableLayout.newTab().setText(model.order_desc).setTag(model));
        }
        //tableLayout
        orderPagerAdapter= new OrderPagerAdapter(getSupportFragmentManager(),DataUtil.orderStatusModels);
        viewPager.setAdapter(orderPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                OrderListActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
