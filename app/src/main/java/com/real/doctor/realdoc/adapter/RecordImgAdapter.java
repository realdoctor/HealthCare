package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.RecordImagesActivity;

import java.util.List;

/**
 * 图片显示RecycleViewAdapter
 * Created by zhujiabin on 2017/6/19 下午3:59.
 * 邮箱:414037481@qq.com
 */

public class RecordImgAdapter extends RecyclerView.Adapter<RecordImgAdapter.RecordViewHolder> {

    private List<String> mDatas;
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;

    public RecordImgAdapter(Context context, List<String> datas) {
        this.mDatas = datas;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecordViewHolder(mLayoutInflater.inflate(R.layout.record_images_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecordViewHolder holder, final int position) {
        if (position >= RecordImagesActivity.IMAGE_SIZE) {//图片已选完时，隐藏添加按钮
            holder.imageView.setVisibility(View.GONE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
        }
        Glide.with(mContext).load(mDatas.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public RecordViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
        }
    }
}
