package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.InfoModel;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;


public class InfoAdapter extends RdBaseAdapter<InfoModel>{
    public InfoAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoModel bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.info_item, parent, false);
            holder.img_show = (ImageView) convertView.findViewById(R.id.img_show);
            holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tv_name.setText(bean.content);
        Glide.with(mContext).load(bean.pic).crossFade().error(R.drawable.timg).into(holder.img_show);
        return convertView;
    }



    public class Holder {
       public ImageView img_show;
       public TextView tv_name;

    }


}
