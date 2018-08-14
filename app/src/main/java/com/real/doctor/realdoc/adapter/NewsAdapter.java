package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.view.WarpLinearLayout;

import org.w3c.dom.Text;

import java.util.List;


public class NewsAdapter extends RdBaseAdapter<NewModel> {

    private Context context;

    public NewsAdapter(Context context, List list) {
        super(context, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewModel bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.my_news_item, parent, false);
            holder.newDetailImg = (ImageView) convertView.findViewById(R.id.new_detail_img);
            holder.newTitle = (TextView) convertView.findViewById(R.id.new_title);
            holder.newTime = (TextView) convertView.findViewById(R.id.new_time);
            holder.commentTv = (TextView) convertView.findViewById(R.id.comment_tv);
            holder.newHospital = convertView.findViewById(R.id.new_hospital);
            holder.hospitalTv = convertView.findViewById(R.id.hospital_tv);
            holder.priceTv = convertView.findViewById(R.id.tv_price);
            holder.llContent = convertView.findViewById(R.id.ll_content);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.newTitle.setText(bean.newsName);
        holder.commentTv.setText(bean.viewedTime);
        holder.newHospital.setText(bean.authorHos);
        if (EmptyUtils.isEmpty(bean.createDate)) {
            holder.newTime.setVisibility(View.GONE);
        } else {
            holder.newTime.setText(DateUtil.timeStamp2Date(bean.createDate, null));
        }
        StringBuffer sb = new StringBuffer();
        sb.append(bean.authorHos);
        sb.append(" ");
        sb.append(bean.newsAuthor);
        sb.append(" ");
        sb.append(bean.authorDept);
        holder.hospitalTv.setText(sb.toString());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.llContent.removeAllViews();
        for (int i = 0; i < bean.tagsList.size(); i++) {
            TextView textView = new TextView(context);
            layoutParams.setMargins(0, 5, 10, 5);
            textView.setTextSize(12);
            textView.setBackgroundResource(R.drawable.order_bg); //设置背景
            textView.setText(bean.tagsList.get(i).newsTag);
            textView.setLayoutParams(layoutParams);
            holder.llContent.addView(textView);
        }
        if (Double.parseDouble(bean.price) == 0.00) {
            holder.priceTv.setVisibility(View.GONE);
        } else {
            holder.priceTv.setVisibility(View.VISIBLE);
            holder.priceTv.setText("收费金额：" + bean.price);
        }
//        Glide.with(mContext).load(bean.photoAddress).crossFade().error(R.drawable.timg).into(holder.new_detail_img);
        return convertView;
    }

    public class Holder {
        public ImageView newDetailImg;
        public TextView newTitle;
        public TextView newTime;
        public TextView commentTv;
        public TextView newHospital;
        public TextView hospitalTv;
        public TextView priceTv;
        public WarpLinearLayout llContent;
    }
}
