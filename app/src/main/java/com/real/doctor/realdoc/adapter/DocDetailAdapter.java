package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDetailAdapter extends BaseQuickAdapter<SaveDocBean, BaseViewHolder> {

    private Context context;

    public DocDetailAdapter(Context context, @LayoutRes int layoutResId, List<SaveDocBean> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, SaveDocBean item) {
        viewHolder.setText(R.id.doc_detail_title, item.getIll())
                .setText(R.id.doc_detail_content, item.getHospital())
                .setText(R.id.doc_detail_time, DateUtil.timeStamp2Date(item.getTime(), "y年M月d日"));
        if (EmptyUtils.isNotEmpty(item) && EmptyUtils.isNotEmpty(item.getFolder())) {
            String folder = item.getFolder().toString().trim();
            String[] imgs = item.getImgs().split(";");
            if (EmptyUtils.isNotEmpty(imgs)) {
                String path = SDCardUtils.getPictureDir() + folder + File.separator + imgs[0];
                Glide.with(mContext).load(path).crossFade().into((ImageView) viewHolder.getView(R.id.doc_detail_img));
            }
        } else {
            Glide.with(mContext).load("").crossFade().into((ImageView) viewHolder.getView(R.id.doc_detail_img));
        }
    }
}
