package com.real.doctor.realdoc.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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
        if (EmptyUtils.isNotEmpty(advice)) {
            viewHolder.setText(R.id.video_advice, item.getAdvice());
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        int pos = viewHolder.getLayoutPosition();// 获取当前item的position
        viewHolder.setText(R.id.name, "Video" + String.valueOf(pos + ".mp4")).setText(R.id.time, item.getElapsedMillis()).setImageBitmap(R.id.video_img, bitmap);
    }
}
