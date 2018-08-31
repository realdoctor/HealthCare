package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        helper.setText(R.id.patient_name, item.getName())
                .setText(R.id.disease, item.getTitle())
                .setText(R.id.add_time, DateUtil.timeStamp2Date(item.getAddTime(), "yyyy年MM月dd日 HH:mm"));
        Glide.with(mContext).load(item.getPatientImageUrl()).crossFade().into((ImageView) helper.getView(R.id.patient_img));
        String status = item.getStatus();
        TextView revisiting = helper.getView(R.id.revisiting);
        TextView revisited = helper.getView(R.id.revisited);
        if (status.equals("1")) {
            revisiting.setVisibility(View.VISIBLE);
            revisited.setVisibility(View.GONE);
        } else if (status.equals("2")) {
            revisiting.setVisibility(View.GONE);
            revisited.setVisibility(View.VISIBLE);
        }
    }
}
