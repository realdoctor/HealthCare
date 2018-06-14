package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SDCardUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujiabin on 2018/4/25.
 */

public class CheckDocAdapter extends RecyclerView.Adapter<CheckDocAdapter.ViewHolder> {

    private Context context;
    private List<SaveDocBean> mSaveDocBean;
    private OnItemClickListener mOnItemClickListener;

    public CheckDocAdapter(Context context) {
        this.context = context;
    }

    public void notifyAdapter(List<SaveDocBean> saveDocBeanList, boolean isAdd) {

        if (!isAdd) {
            this.mSaveDocBean = saveDocBeanList;
        } else {
            this.mSaveDocBean.addAll(saveDocBeanList);
        }
        //将所有已选中去除
        for (int i = 0; i < mSaveDocBean.size(); i++) {
            if (mSaveDocBean.get(i).getIsSelect()) {
                mSaveDocBean.get(i).setIsSelect(false);
            }
        }
        notifyDataSetChanged();
    }

    public List<SaveDocBean> getSaveDocBeanList() {
        if (mSaveDocBean == null) {
            mSaveDocBean = new ArrayList<>();
        }
        return mSaveDocBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_doc_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mSaveDocBean.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SaveDocBean saveDocBean = mSaveDocBean.get(holder.getAdapterPosition());
        holder.mTvTitle.setText(saveDocBean.getIll());
        holder.mTvContent.setText(saveDocBean.getHospital());
        holder.mTvTime.setText(DateUtil.timeStamp2Date(saveDocBean.getTime(), "yyyy年MM月dd日"));
        if (saveDocBean.getIsSelect()) {
            holder.mCheckBox.setImageResource(R.mipmap.ic_checked);
        } else {
            holder.mCheckBox.setImageResource(R.mipmap.ic_uncheck);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mSaveDocBean);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<SaveDocBean> saveDocBeanList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_content)
        TextView mTvContent;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.check_box)
        ImageView mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
