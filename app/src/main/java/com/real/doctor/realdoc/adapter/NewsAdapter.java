package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;

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
            holder.tv_content=(TextView)convertView.findViewById(R.id.tv_content);
            holder.tv_comment=(TextView) convertView.findViewById(R.id.tv_comment);


            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.new_title.setText(bean.newsName);
        holder.new_autor.setText(bean.newsAuthor);
        holder.new_time.setText(DateUtil.timeStamp2Date(bean.createDate,null));
        holder.tv_comment.setText(bean.viewedTime);
//        holder.tv_content.setText();
        Glide.with(mContext).load(bean.photoAddress).crossFade().into(holder.new_detail_img);
        return convertView;
    }



    public class Holder {
       public ImageView new_detail_img;
       public TextView new_title;
       public TextView new_autor;
       public TextView new_time;
       public TextView tv_content;
       public TextView tv_comment;
    }


}
