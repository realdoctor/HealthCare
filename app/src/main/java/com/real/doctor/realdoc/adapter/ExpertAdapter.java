package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.HospitalBean;

import java.util.List;


public class ExpertAdapter extends RdBaseAdapter<ExpertBean> {
    public ExpertAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExpertBean bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.expert_item, parent, false);
            holder.expert_detail_img = (ImageView) convertView.findViewById(R.id.expert_detail_img);
            holder.expert_title=(TextView) convertView.findViewById(R.id.expert_title);
            holder.expert_postion=(TextView) convertView.findViewById(R.id.expert_postion);
            holder.expert_level=(TextView) convertView.findViewById(R.id.expert_level);
            holder.expert_markNum=(TextView) convertView.findViewById(R.id.expert_markNum);
            holder.good_at=(TextView) convertView.findViewById(R.id.good_at);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.expert_title.setText(bean.doctorName);
        holder.expert_postion.setText(bean.positional);
        holder.expert_markNum.setText("接诊量："+bean.receiveNum);
        holder.good_at.setText(bean.goodAt);
        Glide.with(mContext).load(bean.expertImage).crossFade().into((ImageView) holder.expert_detail_img);
        return convertView;
    }

    public class Holder {
       public ImageView expert_detail_img;
       public TextView expert_title;
       public TextView expert_postion;
       public TextView hospital_markNum;
       public TextView expert_level;
       public TextView expert_markNum;
       public TextView good_at;
    }
}
