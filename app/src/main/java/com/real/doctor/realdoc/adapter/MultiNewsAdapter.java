package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.AdBean;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiNewsAdapter extends BaseAdapter{
    //itemA类的type标志
    private static final int TYPE_A = 0;
    //itemB类的type标志
    private static final int TYPE_B = 1;

    private Context context;
    //整合数据
    private List<Object> data = new ArrayList<>();


    public MultiNewsAdapter(Context context, ArrayList<Object> as ,ArrayList<Object> bs) {
        this.context = context;

        //把数据装载同一个list里面
        data.addAll(as);
        data.addAll(bs);

        Collections.shuffle(data); // 混乱的意思

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建两种不同种类的viewHolder变量
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        //根据position获得View的type
        int type = getItemViewType(position);
        if (convertView == null) {
            //实例化
            holder1 = new ViewHolder1();
            holder2 = new ViewHolder2();
            //根据不同的type 来inflate不同的item layout
            //然后设置不同的tag
            //这里的tag设置是用的资源ID作为Key
            switch (type) {
                case TYPE_A:
                    convertView = View.inflate(context, R.layout.news_item, null);
                    holder1.new_detail_img = (ImageView) convertView.findViewById(R.id.new_detail_img);
                    holder1.new_title=(TextView) convertView.findViewById(R.id.new_title);
                    holder1.new_time=(TextView) convertView.findViewById(R.id.new_time);
                    holder1.tv_content=(TextView)convertView.findViewById(R.id.tv_content);
                    holder1.tv_comment=(TextView) convertView.findViewById(R.id.tv_comment);
                    holder1.new_hospital=convertView.findViewById(R.id.new_hospital);
                    holder1.tv_newsAuthor=convertView.findViewById(R.id.tv_newsAuthor);
                    holder1.tv_authorDept=convertView.findViewById(R.id.tv_authorDept);
                    holder1.tv_hospital=convertView.findViewById(R.id.tv_hospital);
                    convertView.setTag(R.id.tag_first, holder1);
                    break;
                case TYPE_B:
                    convertView = View.inflate(context, R.layout.activity_ad_list_item, null);
                    holder2.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder2.img = (ImageView) convertView.findViewById(R.id.img_show);
                    convertView.setTag(R.id.tag_second, holder2);
                    break;
            }

        } else {
            //根据不同的type来获得tag
            switch (type) {
                case TYPE_A:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.tag_first);
                    break;
                case TYPE_B:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.tag_second);
                    break;
            }
        }

        Object o = data.get(position);
        //根据不同的type设置数据
        switch (type) {
            case TYPE_A:
                NewModel bean = (NewModel) o;
                holder1.new_title.setText(bean.newsName);
                holder1.new_time.setText(DateUtil.timeStamp2Date(bean.createDate,null));
                holder1.tv_comment.setText(bean.viewedTime);
                holder1.new_hospital.setText(bean.authorHos);
                holder1.tv_newsAuthor.setText(bean.newsAuthor);
                holder1.tv_authorDept.setText(bean.authorDept);
                holder1.tv_hospital.setText(bean.authorHos);
                Glide.with(context).load(bean.photoAddress).crossFade().error(R.drawable.timg).into(holder1.new_detail_img);
                break;

            case TYPE_B:
                AdBean b = (AdBean) o;
                holder2.tv_name.setText(b.content);
                Glide.with(context).load(b.pic).crossFade().error(R.drawable.timg).into(holder2.img);
                break;
        }
        return convertView;
    }




    /**
     * 获得itemView的type
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (data.get(position) instanceof NewModel) {
            result = TYPE_A;
        } else if (data.get(position) instanceof AdBean) {
            result = TYPE_B;
        }
        return result;
    }

    /**
     * 获得有多少中view type
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder1 {
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
    /**
     * item B 的Viewholder
     */
    private static class ViewHolder2 {
        TextView tv_name;
        ImageView img;
    }
}
