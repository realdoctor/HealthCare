package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.PushInfoBean;

import java.util.List;

public class InfoAdapter extends BaseQuickAdapter<PushInfoBean, BaseViewHolder> {

    public InfoAdapter(int layoutResId, @Nullable List<PushInfoBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PushInfoBean item) {
        helper.setText(R.id.title, item.getTitle()).setText(R.id.info, item.getContent()).setText(R.id.time, item.getTime());
    }
}
