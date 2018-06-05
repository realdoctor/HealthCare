package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.NewModel;

import java.util.List;


public class NewsAdapter extends RdBaseAdapter<NewModel>{
    public NewsAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewModel bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.news_item, parent, false);
            holder.new_detail_img = (ImageView) convertView.findViewById(R.id.new_detail_img);
            holder.new_title=(TextView) convertView.findViewById(R.id.new_title);
            holder.new_autor=(TextView) convertView.findViewById(R.id.new_autor);
            holder.new_time=(TextView) convertView.findViewById(R.id.new_time);
            holder.new_type=(TextView) convertView.findViewById(R.id.new_type);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.new_title.setText(bean.newsName);
        holder.new_autor.setText(bean.newsAuthor);
        holder.new_time.setText(bean.createDate);
        holder.new_type.setText(bean.newsType);
        Glide.with(mContext).load(bean.photoAddress).crossFade().into((ImageView) holder.new_detail_img);
        return convertView;
    }



    public class Holder {
       public ImageView new_detail_img;
       public TextView new_title;
       public TextView new_autor;
       public TextView new_time;
       public TextView new_type;
    }


}
