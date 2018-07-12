package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.InqueryActivity;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocPayListAdapter extends BaseQuickAdapter<DoctorBean, BaseViewHolder> {

    private Context context;
    private boolean btnFlag = false;

    public DocPayListAdapter(Context context, @LayoutRes int layoutResId, List<DoctorBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    public DocPayListAdapter(Context context, @LayoutRes int layoutResId, List<DoctorBean> data, boolean btnFlag) {
        super(layoutResId, data);
        this.context = context;
        this.btnFlag = btnFlag;
    }

    @Override
    protected void convert(BaseViewHolder helper, final DoctorBean item) {
        helper.setText(R.id.doctor_name, item.getName())
                .setText(R.id.visit_date, DateUtil.timeStamp2Date(item.getAddTime(), "yyyy年MM月dd日"))
                .setText(R.id.inquery, item.getInquery());

        Glide.with(mContext).load(item.getAvater()).crossFade().into((ImageView) helper.getView(R.id.doctor_img));
        Button btn = helper.getView(R.id.revisit);
        if (btnFlag) {
            if (item.getRetryNum().equals("0")) {
                btn.setVisibility(View.GONE);
            } else {
                btn.setVisibility(View.VISIBLE);
            }
        } else {
            btn.setVisibility(View.GONE);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击咨询按钮
                //点击进入病历列表页面
                Intent intent = new Intent(context, InqueryActivity.class);
                intent.putExtra("doctorUserId", item.getId());
                intent.putExtra("questionId", item.getQuestionId());
                intent.putExtra("desease", item.getDiagName());
                context.startActivity(intent);
            }
        });
    }
}
