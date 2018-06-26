package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseFragment;

/**
 * Created by Administrator on 2018/4/18.
 */

public class MessageFragment extends BaseFragment {
    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_message;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
}
