package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocPayListAdapter extends BaseQuickAdapter<PatientBean, BaseViewHolder> {

    private Context context;

    public DocPayListAdapter(Context context, @LayoutRes int layoutResId, List<PatientBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, PatientBean item) {
        helper.setText(R.id.patient_name, item.getUserInfo().getRealname())
                .setText(R.id.add_time, DateUtil.timeStamp2Date(item.getAddTime(), "yyyy年MM月dd日"));
        Glide.with(mContext).load(item.getUserInfo().getAvater()).crossFade().into((ImageView) helper.getView(R.id.patient_img));
    }
}
