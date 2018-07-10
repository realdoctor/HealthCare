package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlreadyRevisitFragment extends BaseFragment {

    private Unbinder unbinder;

    @Override
    public int getLayoutId() {
        return R.layout.already_revisit_frag;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
