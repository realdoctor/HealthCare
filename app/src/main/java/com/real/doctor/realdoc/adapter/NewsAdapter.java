package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.view.WarpLinearLayout;

import java.util.List;

public class NewsAdapter extends BaseQuickAdapter<NewModel, BaseViewHolder> {


    public NewsAdapter(int layoutResId, @Nullable List<NewModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, NewModel item) {
        viewHolder.setText(R.id.new_title, item.newsName).setText(R.id.comment_tv, item.viewedTime).setText(R.id.new_hospital, item.authorHos);
        TextView newTime = viewHolder.getView(R.id.new_time);
        if (EmptyUtils.isEmpty(item.createDate)) {
            newTime.setVisibility(View.GONE);
        } else {
            newTime.setText(DateUtil.timeStamp2Date(item.createDate, "yyyy年MM月dd日 HH:mm"));
        }
        TextView hospitalTv = viewHolder.getView(R.id.hospital_tv);
        StringBuffer sb = new StringBuffer();
        sb.append(item.authorHos);
        sb.append(" ");
        sb.append(item.newsAuthor);
        sb.append(" ");
        sb.append(item.authorDept);
        hospitalTv.setText(sb.toString());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        WarpLinearLayout llContent = viewHolder.getView(R.id.ll_content);
        llContent.removeAllViews();
        for (int i = 0; i < item.tagsList.size(); i++) {
            TextView textView = new TextView(mContext);
            layoutParams.setMargins(0, 5, 10, 5);
            textView.setTextSize(12);
            textView.setBackgroundResource(R.drawable.order_bg); //设置背景
            textView.setText(item.tagsList.get(i).newsTag);
            textView.setLayoutParams(layoutParams);
            llContent.addView(textView);
        }
        TextView priceTv = viewHolder.getView(R.id.tv_price);
        if (EmptyUtils.isNotEmpty(item.price) && Double.parseDouble(item.price) == 0.00) {
            priceTv.setVisibility(View.GONE);
        } else {
            priceTv.setVisibility(View.VISIBLE);
            priceTv.setText("收费金额：" + item.price);
        }
        // viewHolder.setImageBitmap(R.id.video_img, item.authorHos)图片
        //Glide.with(mContext).load(bean.photoAddress).crossFade().error(R.drawable.timg).into(holder.new_detail_img);
    }
}
