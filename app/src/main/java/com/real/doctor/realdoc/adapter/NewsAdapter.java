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

import org.w3c.dom.Text;

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
            holder.new_time=(TextView) convertView.findViewById(R.id.new_time);
            holder.tv_content=(TextView)convertView.findViewById(R.id.tv_content);
            holder.tv_comment=(TextView) convertView.findViewById(R.id.tv_comment);
            holder.new_hospital=convertView.findViewById(R.id.new_hospital);
            holder.tv_newsAuthor=convertView.findViewById(R.id.tv_newsAuthor);
            holder.tv_authorDept=convertView.findViewById(R.id.tv_authorDept);
            holder.tv_hospital=convertView.findViewById(R.id.tv_hospital);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.new_title.setText(bean.newsName);
        holder.new_time.setText(DateUtil.timeStamp2Date(bean.createDate,null));
        holder.tv_comment.setText(bean.viewedTime);
        holder.new_hospital.setText(bean.authorHos);
        holder.tv_newsAuthor.setText(bean.newsAuthor);
        holder.tv_authorDept.setText(bean.authorDept);
        holder.tv_hospital.setText(bean.authorHos);
//        holder.tv_content.setText();
        Glide.with(mContext).load(bean.photoAddress).crossFade().error(R.drawable.timg).into(holder.new_detail_img);
        return convertView;
    }



    public class Holder {
       public ImageView new_detail_img;
       public TextView new_title;
       public TextView new_time;
       public TextView tv_content;
       public TextView tv_comment;
       public TextView new_hospital;
       public TextView tv_newsAuthor;
       public TextView tv_authorDept;
       public TextView tv_hospital;

    }


}
