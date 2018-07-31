package com.real.doctor.realdoc.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.List;

public class VideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    public VideoAdapter(int layoutResId, @Nullable List<VideoBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, VideoBean item) {
        //从名称中截取时间
        String path = item.getFilePath();
        Bitmap bitmap = DocUtils.getVideoThumbnail(path);
        String advice = item.getAdvice();
        TextView textView = viewHolder.getView(R.id.video_advice);
        ImageView imageView = viewHolder.getView(R.id.recipel_icon);
        if (EmptyUtils.isNotEmpty(advice)) {
            viewHolder.setText(R.id.video_advice, item.getAdvice());
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        int pos = viewHolder.getLayoutPosition();// 获取当前item的position
        viewHolder.setText(R.id.name, "Video" + String.valueOf(pos + ".mp4")).setText(R.id.time, item.getElapsedMillis()).setImageBitmap(R.id.video_img, bitmap);
        int spare = item.getSpareImage();
        if (spare == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            String labelPath = "android.resource://" + mContext.getApplicationContext().getPackageName() + "/";
            if (spare == 1) {
                //处方标签
                Glide.with(mContext).load(labelPath + R.mipmap.add).crossFade().into(imageView);
            } else if (spare == 2) {
                //医嘱标签
                Glide.with(mContext).load(labelPath + R.mipmap.bg_healthy).crossFade().into(imageView);
            } else if (spare == 3) {
                //体征标签
                Glide.with(mContext).load(labelPath + R.mipmap.avatar_bg).crossFade().into(imageView);
            } else if (spare == 4) {
                //报告检查标签
                Glide.with(mContext).load(labelPath + R.mipmap.arrow_white).crossFade().into(imageView);
            }
        }
    }
}
