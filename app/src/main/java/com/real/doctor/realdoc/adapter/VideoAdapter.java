package com.real.doctor.realdoc.adapter;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DocUtils;

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

        viewHolder.setText(R.id.name, item.getFileName()).setText(R.id.time, item.getElapsedMillis()).setImageBitmap(R.id.video_img, bitmap);
    }
}
