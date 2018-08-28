package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by Administrator on 2018/4/27.
 */

public class SingleCompareAdapter extends RecyclerView.Adapter<SingleCompareAdapter.ViewHolder> {

    private RecyclerView mRv;//实现单选方法三： RecyclerView另一种定向刷新方法：
    private static final int saveDocBean_MODE_CHECK = 0;
    int mEditMode = saveDocBean_MODE_CHECK;
    private Context context;
    private List<SaveDocBean> mSaveDocBean;
    private OnItemClickListener mOnItemClickListener;
    private int mSelectedPos = -1;

    public SingleCompareAdapter(Context context, RecyclerView rv, List<SaveDocBean> saveDocBeanList) {
        this.context = context;
        mRv = rv;
        mSaveDocBean = saveDocBeanList;
        mSaveDocBean.get(0).setIsSelect(true);
        for (int i = 0; i < mSaveDocBean.size(); i++) {
            if (mSaveDocBean.get(i).getIsSelect()) {
                mSelectedPos = i;
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_compare_item, parent, false);
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
        holder.mTvTime.setText(DateUtil.timeStamp2Date(saveDocBean.getTime(), "yyyy年MM月dd日 HH:mm"));
        Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        holder.mCheckBox.setSelected(mSaveDocBean.get(position).getIsSelect());
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder viewHolder = (ViewHolder) mRv.findViewHolderForLayoutPosition(mSelectedPos);
                if (viewHolder != null) {//还在屏幕里
                    viewHolder.mCheckBox.setSelected(false);
                } else {
                    //add by 2016 11 22 for 一些极端情况，holder被缓存在Recycler的cacheView里，
                    //此时拿不到ViewHolder，但是也不会回调onBindViewHolder方法。所以add一个异常处理
                    notifyItemChanged(mSelectedPos);
                }
                mSaveDocBean.get(mSelectedPos).setIsSelect(false);//不管在不在屏幕里 都需要改变数据
                //设置新Item的勾选状态
                mSelectedPos = position;
                mSaveDocBean.get(mSelectedPos).setIsSelect(true);
                holder.mCheckBox.setSelected(true);
                mOnItemClickListener.onItemClickListener(mSelectedPos);
            }
        });
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
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