package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.widget.refreshrecyclerview.base.adapter.BaseRecyclerViewAdapter;
import com.real.doctor.realdoc.widget.refreshrecyclerview.base.adapter.BaseViewHolder;
import com.real.doctor.realdoc.widget.refreshrecyclerview.pulltorefresh.PullToRefreshRecyclerView;

import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class ProductItemAdapter extends BaseRecyclerViewAdapter<ProductBean> {
    private View.OnClickListener onClickListener;
    public ProductItemAdapter(Context mContext, List<ProductBean> mDatas, View.OnClickListener onClickListener){
        super(mContext, R.layout.product_detail_item, mDatas);
        this.onClickListener=onClickListener;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ProductBean productBean) {
        baseViewHolder.setText(R.id.product_detail_title,productBean.product_name)
                .setText(R.id.product_detail_content,productBean.product_detail)
                .setText(R.id.product_price,"￥"+productBean.product_price);
        Glide.with(mContext).load(productBean.product_show_pic_url).crossFade().into((ImageView) baseViewHolder.getView(R.id.product_detail_img));
    }
}
