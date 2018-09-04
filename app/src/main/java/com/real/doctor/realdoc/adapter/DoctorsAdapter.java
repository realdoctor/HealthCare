package com.real.doctor.realdoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ChatPayActivity;
import com.real.doctor.realdoc.activity.DoctorsListActivity;
import com.real.doctor.realdoc.activity.OrderExpertByDateActivity;
import com.real.doctor.realdoc.activity.RegistrationsActivity;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CircleImageView;

import java.util.List;

public class DoctorsAdapter extends BaseQuickAdapter<DoctorBean, BaseViewHolder> {

    private Context context;

    public DoctorsAdapter(Context context, int layoutResId, @Nullable List<DoctorBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final DoctorBean item) {
        helper.setText(R.id.doctor_name, item.getRespDoctorName())
                .setText(R.id.hospital_name, item.getVisitOrgName())
                .setText(R.id.desease_name, item.getDiagName())
                .setText(R.id.visit_date, DateUtil.timeStamp2Date(item.getVisitDtime(), "yyyy年MM月dd日 HH:mm"));
        ImageView imageView = helper.getView(R.id.doctor_avater);
        GlideUtils.loadImageViewDiskCache(mContext,item.getAvater(), imageView);
        CircleImageView chatView = helper.getView(R.id.chat);
        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击进入付款页面
                Intent intent = new Intent(context, ChatPayActivity.class);
                intent.putExtra("payType", "1");
                intent.putExtra("doctorUserId", item.getId());
                intent.putExtra("desease", item.getDiagName());
                intent.putExtra("respDoctorName", item.getRespDoctorName());
                intent.putExtra("imageUrl", item.getAvater());
                intent.putExtra("patientRecordId", item.getPatientRecordId());
                intent.putExtra("mobile", item.getMobile());
                context.startActivity(intent);
            }
        });

        CircleImageView uploadView = helper.getView(R.id.upload);
        uploadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击进入付款页面
                Intent intent = new Intent(context, ChatPayActivity.class);
                intent.putExtra("payType", "2");
                intent.putExtra("doctorUserId", item.getId());
                intent.putExtra("desease", item.getDiagName());
                intent.putExtra("patientRecordId", item.getPatientRecordId());
                context.startActivity(intent);
            }
        });
        CircleImageView registrationsView = helper.getView(R.id.registrations);
        registrationsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderExpertByDateActivity.class);
                intent.putExtra("hospitalId", item.getHospitalId());
                intent.putExtra("doctorCode", item.getDoctorCode());
                intent.putExtra("deptCode", item.getDeptCode());
                ((Activity) context).startActivityForResult(intent, DoctorsListActivity.REQUEST_CODE_NO_EXPERT);
            }
        });
    }
}