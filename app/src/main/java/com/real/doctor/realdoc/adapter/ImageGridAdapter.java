package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.List;

public class ImageGridAdapter extends BaseQuickAdapter<ImageBean, BaseViewHolder> {
    public ImageGridAdapter(int layoutResId, @Nullable List<ImageBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, ImageBean item) {
        String advice = item.getAdvice();
        if (EmptyUtils.isNotEmpty(advice)) {
            viewHolder.getView(R.id.advice).setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.advice, advice);
        } else {
            viewHolder.getView(R.id.advice).setVisibility(View.GONE);
        }
        ImageView imageView = viewHolder.getView(R.id.recipel_icon);
        int spare = item.getSpareImage();
        if (spare == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
            String path = "android.resource://" +mContext.getApplicationContext().getPackageName() + "/";
            if (spare == 1) {
                //处方标签
                Glide.with(mContext).load(path + R.mipmap.add).crossFade().into(imageView);
            } else if (spare == 2) {
                //医嘱标签
                Glide.with(mContext).load(path + R.mipmap.bg_healthy).crossFade().into(imageView);
            } else if (spare == 3) {
                //体征标签
                Glide.with(mContext).load(path + R.mipmap.avatar_bg).crossFade().into(imageView);
            } else if (spare == 4) {
                //报告检查标签
                Glide.with(mContext).load(path + R.mipmap.arrow_white).crossFade().into(imageView);
            }
        }
        if (EmptyUtils.isNotEmpty(item.getImgUrl())) {
            Glide.with(mContext).load(item.getImgUrl()).crossFade().into((ImageView) viewHolder.getView(R.id.grid_image));
        } else {
            Glide.with(mContext).load("").crossFade().into((ImageView) viewHolder.getView(R.id.grid_image));
        }
    }
}
