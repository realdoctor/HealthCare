package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.List;


public class ExpertByDateAdapter extends RdBaseAdapter<ExpertBean> implements View.OnClickListener{
    private MyClickListener mListener;
    public ExpertByDateAdapter(Context context, List list, MyClickListener mListener) {
        super(context, list);
        this.mListener=mListener;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExpertBean bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.expert_by_dateitem, parent, false);
            holder.expert_detail_img = (ImageView) convertView.findViewById(R.id.expert_detail_img);
            holder.expert_title=(TextView) convertView.findViewById(R.id.expert_title);
            holder.expert_postion=(TextView) convertView.findViewById(R.id.expert_postion);
            holder.expert_level=(TextView) convertView.findViewById(R.id.expert_level);
            holder.expert_order=convertView.findViewById(R.id.tv_order_expert);
            holder.good_at=(TextView) convertView.findViewById(R.id.expert_good_at);
            holder.tv_plan=(TextView) convertView.findViewById(R.id.tv_plan);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.expert_title.setText(bean.doctorName);
        holder.expert_postion.setText(bean.positional);
        holder.good_at.setText(bean.goodAt);
        if(EmptyUtils.isNotEmpty(bean.orderFlag)&&bean.orderFlag.equals("0")) {
            holder.expert_order.setText("预  约");
            holder.expert_order.setOnClickListener(this);
            holder.expert_order.setTag(bean);
        }else{
            holder.expert_order.setText("预约已满");
        }
        holder.tv_plan.setText(DateUtil.timeStamp2Date(bean.dutyDtime,"yyyy年MM月dd日 HH:mm")+" "+bean.plan);
        Glide.with(mContext).load(bean.expertImage).crossFade().into((ImageView) holder.expert_detail_img);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        mListener.clickListener(v);
    }

    public class Holder {
       public ImageView expert_detail_img;
       public TextView expert_title;
       public TextView expert_postion;
       public TextView expert_order;
       public TextView expert_level;
       public TextView good_at;
       public TextView tv_plan;
    }


    //自定义接口，用于回调按钮点击事件到Activity
    public interface MyClickListener{
        public void clickListener(View v);
    }
}
