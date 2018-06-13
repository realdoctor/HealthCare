package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;

public class HomeRecordAdapter extends BaseQuickAdapter<SaveDocBean, BaseViewHolder> {
    public HomeRecordAdapter(int layoutResId, @Nullable List<SaveDocBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, SaveDocBean item) {
        viewHolder.setText(R.id.disease, item.getIll()).setText(R.id.hospital, item.getHospital())
                .setText(R.id.doctor, item.getDoctor()).setText(R.id.time,
                DateUtil.timeStamp2Date(item.getTime(), "yyyy年MM月dd日"));
    }
}
