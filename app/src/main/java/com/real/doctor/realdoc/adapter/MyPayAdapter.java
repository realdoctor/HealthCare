package com.real.doctor.realdoc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.MyPayBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.view.CircleImageView;

import java.util.List;

public class MyPayAdapter extends BaseQuickAdapter<MyPayBean, BaseViewHolder> {

    public MyPayAdapter(int layoutResId, @Nullable List<MyPayBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MyPayBean item) {
        viewHolder.setText(R.id.pay_title, item.getUserName())
                .setText(R.id.pay, "- ￥" + item.getMoney())
                .setText(R.id.pay_type_title, "[" + item.getType() + "]")
                .setText(R.id.pay_type, item.getPayFrom())
                .setText(R.id.pay_time, DateUtil.timeStamp2Date(item.getAddTime(), "yyyy年MM月dd日 HH:mm"));
        CircleImageView imageView = viewHolder.getView(R.id.user_avator);
        GlideUtils.loadImageViewLoding(mContext, item.getToUserPicUrl(), imageView, R.mipmap.ease_default_avatar, R.mipmap.ease_default_avatar);
    }
}
