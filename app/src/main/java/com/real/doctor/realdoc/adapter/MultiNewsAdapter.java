package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.AdBean;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.view.WarpLinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MultiNewsAdapter extends BaseAdapter {
    //itemA类的type标志
    public static final int TYPE_A = 0;
    //itemB类的type标志
    public static final int TYPE_B = 1;
    private Context context;
    //整合数据
    private List<Object> data = new ArrayList<>();

    public MultiNewsAdapter(Context context, ArrayList<Object> as, ArrayList<Object> bs) {
        this.context = context;
        //把数据装载同一个list里面
        data.addAll(as);
        //我看内容都重复,所以不要混乱了，广告先不要
//        data.addAll(bs);
//        Collections.shuffle(data); // 混乱的意思
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
                    holder1.newDetailImg = (ImageView) convertView.findViewById(R.id.new_detail_img);
                    holder1.newTitle = (TextView) convertView.findViewById(R.id.new_title);
                    holder1.newTime = (TextView) convertView.findViewById(R.id.new_time);
                    holder1.llContent = convertView.findViewById(R.id.ll_content);
                    holder1.commentTv = (TextView) convertView.findViewById(R.id.comment_tv);
                    holder1.newHospital = convertView.findViewById(R.id.new_hospital);
                    holder1.hospitalTv = convertView.findViewById(R.id.hospital_tv);
                    holder1.priceTv = convertView.findViewById(R.id.tv_price);
                    convertView.setTag(R.id.tag_first, holder1);
                    break;
                case TYPE_B:
                    convertView = View.inflate(context, R.layout.activity_ad_list_item, null);
                    holder2.nameTv = (TextView) convertView.findViewById(R.id.tv_name);
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
                holder1.newTitle.setText(bean.newsName);
                if (EmptyUtils.isEmpty(bean.createDate)) {
                    holder1.newTime.setVisibility(View.GONE);
                } else {
                    holder1.newTime.setText(DateUtil.timeStamp2Date(bean.createDate, "yyyy年MM月dd日 HH:mm"));
                }
                holder1.commentTv.setText(bean.viewedTime);
                holder1.newHospital.setText(bean.authorHos);
                StringBuffer sb = new StringBuffer();
                sb.append(bean.authorHos);
                sb.append(" ");
                sb.append(bean.newsAuthor);
                sb.append(" ");
                sb.append(bean.authorDept);
                holder1.hospitalTv.setText(sb.toString());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                holder1.llContent.removeAllViews();
                for (int i = 0; i < bean.tagsList.size(); i++) {
                    TextView textView = new TextView(context);
                    layoutParams.setMargins(0, 5, 10, 5);
                    textView.setTextSize(12);
                    textView.setBackgroundResource(R.drawable.order_bg); //设置背景
                    textView.setText(bean.tagsList.get(i).newsTag);
                    textView.setLayoutParams(layoutParams);
                    holder1.llContent.addView(textView);
                }
                if (Double.parseDouble(bean.price) == 0.00) {
                    holder1.priceTv.setVisibility(View.GONE);
                } else {
                    holder1.priceTv.setVisibility(View.VISIBLE);
                    holder1.priceTv.setText("收费金额：" + bean.price);
                }
//                Glide.with(context).load(bean.photoAddress).crossFade().error(R.mipmap.zhifubao_select).into(holder1.new_detail_img);
                break;

            case TYPE_B:
                AdBean b = (AdBean) o;
                holder2.nameTv.setText(b.content);
//                Glide.with(context).load(b.pic).crossFade().error(R.mipmap.zhifubao_select).into(holder2.img);
                break;
        }
        return convertView;
    }


    /**
     * 获得itemView的type
     *
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
     *
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
        public ImageView newDetailImg;
        public TextView newTitle;
        public TextView newTime;
        public WarpLinearLayout llContent;
        public TextView commentTv;
        public TextView newHospital;
        public TextView hospitalTv;
        public TextView priceTv;
    }

    /**
     * item B 的Viewholder
     */
    private static class ViewHolder2 {
        TextView nameTv;
        ImageView img;
    }
}
