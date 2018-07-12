package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.InfoBean;

import java.util.List;

public class InfoAdapter extends BaseQuickAdapter<InfoBean, BaseViewHolder> {

    public InfoAdapter(int layoutResId, @Nullable List<InfoBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InfoBean item) {
        helper.setText(R.id.info, item.getContent());
    }
}
