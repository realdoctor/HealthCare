package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.HospitalBean;
import java.util.List;


public class HospitalAdapter extends RdBaseAdapter<HospitalBean> {
    public HospitalAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HospitalBean bean = getItem(position);
        final HospitalHolder holder;
        if (convertView == null) {
            holder = new HospitalHolder();
            convertView = mInflater.inflate(R.layout.hospital_item, parent, false);
            holder.hospital_detail_img = (ImageView) convertView.findViewById(R.id.hospital_detail_img);
            holder.hospital_title=(TextView) convertView.findViewById(R.id.hospital_title);
            holder.hospital_level=(TextView) convertView.findViewById(R.id.hospital_level);
            holder.hospital_markNum=(TextView) convertView.findViewById(R.id.hospital_markNum);
            holder.good_at=(TextView) convertView.findViewById(R.id.good_at);
            convertView.setTag(holder);
        } else {
            holder = (HospitalHolder) convertView.getTag();
        }
        holder.hospital_title.setText(bean.hospitalName);
        holder.hospital_level.setText(bean.hospitalLevel);
        holder.hospital_markNum.setText("预约量为："+bean.markNum);
        Glide.with(mContext).load(bean.hospitalImage).crossFade().into((ImageView) holder.hospital_detail_img);
        return convertView;
    }

    public class HospitalHolder {
       public ImageView hospital_detail_img;
       public TextView hospital_title;
       public TextView hospital_level;
       public TextView hospital_markNum;
       public TextView good_at;
    }
}
