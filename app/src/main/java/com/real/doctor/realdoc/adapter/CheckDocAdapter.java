package com.real.doctor.realdoc.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujiabin on 2018/4/25.
 */

public class CheckDocAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
        notifyDataSetChanged();
    }

    public List<SaveDocBean> getSaveDocBeanList() {
        if (mSaveDocBean == null) {
            mSaveDocBean = new ArrayList<>();
        }
        return mSaveDocBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //根据不同的viewType，创建并返回影响的ViewHolder
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case SaveDocBean.TYPE_ONE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_doc_item, parent, false);
                holder = new ViewHolder(view);
                return holder;
            case SaveDocBean.TYPE_TWO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_doc_title_item, parent, false);
                holder = new TitleViewHolder(view);
                return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final SaveDocBean saveDocBean = mSaveDocBean.get(holder.getAdapterPosition());
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).bindHolder(saveDocBean);
            ((ViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mSaveDocBean);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mSaveDocBean.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mSaveDocBean.size();
    }


    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<SaveDocBean> saveDocBeanList);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
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

        //为viewHolder绑定数据
        public void bindHolder(final SaveDocBean saveDocBean) {
            mTvTitle.setText(saveDocBean.getIll());
            mTvContent.setText(saveDocBean.getHospital());
            mTvTime.setText(DateUtil.timeStamp2Date(saveDocBean.getTime(), "yyyy年MM月dd日 HH:mm"));
            if (saveDocBean.getIsSelect()) {
                mCheckBox.setImageResource(R.mipmap.ic_checked);
            } else {
                mCheckBox.setImageResource(R.mipmap.ic_uncheck);
            }
        }
    }

    public static class TitleViewHolder extends RecyclerView.ViewHolder {
        public TitleViewHolder(View itemView) {
            super(itemView);
        }
    }
}
