package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;

public class CaseControlAdapter extends BaseQuickAdapter<PatientBean, BaseViewHolder> {

    public CaseControlAdapter(int layoutResId, @Nullable List<PatientBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PatientBean item) {
        helper.setText(R.id.patient_name, item.getUserInfo().getRealname())
                .setText(R.id.add_time, DateUtil.timeStamp2Date(item.getAddTime(), "yyyy年MM月dd日"));
        Glide.with(mContext).load(item.getUserInfo().getAvater()).crossFade().into((ImageView) helper.getView(R.id.patient_img));
    }
}
