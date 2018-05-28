package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.RegistrationModel;

import java.util.List;


public class RegistrationAdapter extends RdBaseAdapter<RegistrationModel>{
    public RegistrationAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RegistrationModel bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.registration_item, parent, false);
            holder.hospital_detail_img = (ImageView) convertView.findViewById(R.id.hospital_detail_img);
            holder.hospital_title=(TextView) convertView.findViewById(R.id.hospital_title);
            holder.hospital_level=(TextView) convertView.findViewById(R.id.hospital_level);
            holder.expert_level=(TextView) convertView.findViewById(R.id.expert_level);
            holder.registration_time=(TextView) convertView.findViewById(R.id.registration_time);
            holder.doctor_name=(TextView) convertView.findViewById(R.id.doctor_name);
            holder.good_at=(TextView) convertView.findViewById(R.id.good_at);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.hospital_title.setText(bean.hospitalName);
        holder.hospital_level.setText(bean.hospitalLevel);
        holder.doctor_name.setText(bean.doctorName);
        holder.expert_level.setText(bean.positional);
        holder.registration_time.setText(bean.plan);
        holder.good_at.setText(bean.goodAt);
        Glide.with(mContext).load(bean.hospital_detail_img).crossFade().into((ImageView) holder.hospital_detail_img);
        return convertView;
    }



    public class Holder {
       public ImageView hospital_detail_img;
       public TextView hospital_title;
       public TextView hospital_level;
       public TextView registration_time;
       public TextView expert_level;
       public TextView good_at;
       public TextView doctor_name;
    }

}
