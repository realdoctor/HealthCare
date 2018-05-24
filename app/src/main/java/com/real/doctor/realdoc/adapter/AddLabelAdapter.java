package com.real.doctor.realdoc.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.List;

public class AddLabelAdapter extends BaseQuickAdapter<AddLabelBean, BaseViewHolder> {

    public AddLabelAdapter(int layoutResId, @Nullable List<AddLabelBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, AddLabelBean item) {
        viewHolder.setText(R.id.label_name, item.getName());
        String icon = item.getIcon();
        Uri uri = Uri.parse(icon);
        if (EmptyUtils.isNotEmpty(icon)) {
            Glide.with(mContext).load(uri).crossFade().into((ImageView) viewHolder.getView(R.id.label_icon));
        } else {
            Glide.with(mContext).load("").crossFade().into((ImageView) viewHolder.getView(R.id.label_icon));
        }
    }
}
