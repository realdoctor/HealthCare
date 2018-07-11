package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ChatActivity;
import com.real.doctor.realdoc.activity.ChatPayActivity;
import com.real.doctor.realdoc.activity.CheckDocActivity;
import com.real.doctor.realdoc.activity.InqueryActivity;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.model.InqueryBean;
import com.real.doctor.realdoc.util.DateUtil;
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
        helper.setText(R.id.doctor_name, item.getName())
                .setText(R.id.hospital_name, item.getVisitOrgName())
                .setText(R.id.desease_name, item.getDiagName())
                .setText(R.id.visit_date, DateUtil.timeStamp2Date(item.getVisitDtime(), "yyyy年MM月dd日"));
        CircleImageView chatView = helper.getView(R.id.chat);
        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击进入付款页面
                Intent intent = new Intent(context, ChatPayActivity.class);
                intent.putExtra("payType", "1");
                intent.putExtra("doctorUserId", item.getId());
                intent.putExtra("desease", item.getDiagName());
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
                context.startActivity(intent);
            }
        });
    }
}