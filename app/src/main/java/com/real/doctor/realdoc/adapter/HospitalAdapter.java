package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.RegistrationsActivity;
import com.real.doctor.realdoc.model.HospitalBean;
import com.real.doctor.realdoc.util.DistanceUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
            holder.order_expert=(TextView) convertView.findViewById(R.id.order_expert);
            holder.tv_distance=(TextView) convertView.findViewById(R.id.tv_distance);
            convertView.setTag(holder);
        } else {
            holder = (HospitalHolder) convertView.getTag();
        }
        holder.hospital_title.setText(bean.hospitalName);
        holder.hospital_level.setText(bean.hospitalLevel);
        double num=Integer.parseInt(bean.receiveNum);
        DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
        String numShow=num>=10000?String.valueOf(num/10000)+"w":decimalFormat.format(num);
        holder.hospital_markNum.setText("预约量为："+numShow);

        DecimalFormat df = new DecimalFormat("#");
        Double distance=DistanceUtil.getDistance(RegistrationsActivity.latitude,RegistrationsActivity.longitude,Double.parseDouble(bean.lat),Double.parseDouble(bean.lng));
        holder.tv_distance.setText("距"+ df.format(distance/1000)+"公里");
        bean.distance=(Integer) distance.intValue();
        Glide.with(mContext).load(bean.hospitalImage).crossFade().into((ImageView) holder.hospital_detail_img);
        return convertView;
    }

    public class HospitalHolder {
       public ImageView hospital_detail_img;
       public TextView hospital_title;
       public TextView hospital_level;
       public TextView hospital_markNum;
       public TextView order_expert;
       public TextView tv_distance;
    }
}
