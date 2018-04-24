package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/20.
 */

public class SettingActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
