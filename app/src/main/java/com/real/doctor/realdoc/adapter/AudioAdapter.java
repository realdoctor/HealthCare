package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AudioAdapter extends BaseQuickAdapter<RecordBean, BaseViewHolder> {

    public AudioAdapter(int layoutResId, @Nullable List<RecordBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, RecordBean item) {
        //从名称中截取时间
        String path = item.getFilePath();
        String str = path.substring(path.lastIndexOf("/") + 1, path.length());
        String time = str.substring(0, str.indexOf("."));

        viewHolder.setText(R.id.name, item.getFileName())
                .setText(R.id.time, DateUtil.timeStamp2Date(item.getElapsedMillis(), "mm:ss"))
                .setText(R.id.date, DateUtil.timeStamp2Date(String.valueOf(Long.valueOf(time) / 1000), "yyyy年MM月dd日"));

    }
}
