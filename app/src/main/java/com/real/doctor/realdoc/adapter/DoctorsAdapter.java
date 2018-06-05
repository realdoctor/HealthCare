package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.DoctorBean;

import java.util.List;

public class DoctorsAdapter extends BaseQuickAdapter<DoctorBean, BaseViewHolder> {

    public DoctorsAdapter(int layoutResId, @Nullable List<DoctorBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DoctorBean item) {
        helper.setText(R.id.doctor_name, item.getRespDoctorName())
                .setText(R.id.hospital_name, item.getVisitOrgName())
                .setText(R.id.desease_name, item.getDiagName())
                .setText(R.id.visit_date, item.getVisitDtime());
    }
}