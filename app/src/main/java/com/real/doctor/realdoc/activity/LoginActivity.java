package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/18.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.user_register)
    TextView userRegister;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
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
    @OnClick({R.id.user_register})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.user_register:
                actionStart(LoginActivity.this, RegisterActivity.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}
