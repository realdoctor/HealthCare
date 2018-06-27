package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.PatientBean;

import java.util.List;

public class CaseControlAdapter extends BaseQuickAdapter<PatientBean, BaseViewHolder> {

    public CaseControlAdapter(int layoutResId, @Nullable List<PatientBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PatientBean item) {
        helper.setText(R.id.patient_name, item.getName())
                .setText(R.id.hospital_name, item.getVisitOrgName())
                .setText(R.id.desease_name, item.getDiagName())
                .setText(R.id.visit_date, item.getVisitDtime());
    }
}
