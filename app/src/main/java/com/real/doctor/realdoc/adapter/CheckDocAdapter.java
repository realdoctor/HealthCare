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
import com.real.doctor.realdoc.model.CheckDocBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujiabin on 2018/4/25.
 */

public class CheckDocAdapter extends RecyclerView.Adapter<CheckDocAdapter.ViewHolder> {

    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;

    private int secret = 0;
    private String title = "";
    private Context context;
    private List<CheckDocBean> mCheckDocBean;
    private OnItemClickListener mOnItemClickListener;

    public CheckDocAdapter(Context context) {
        this.context = context;
    }


    public void notifyAdapter(List<CheckDocBean> myLiveList, boolean isAdd) {
        if (!isAdd) {
            this.mCheckDocBean = myLiveList;
        } else {
            this.mCheckDocBean.addAll(myLiveList);
        }
        notifyDataSetChanged();
    }

    public List<CheckDocBean> getMyLiveList() {
        if (mCheckDocBean == null) {
            mCheckDocBean = new ArrayList<>();
        }
        return mCheckDocBean;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_doc_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mCheckDocBean.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CheckDocBean myLive = mCheckDocBean.get(holder.getAdapterPosition());
        holder.mTvTitle.setText(myLive.getIll());
        holder.mTvSource.setText(myLive.getHospital());
        if (mEditMode == MYLIVE_MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);

            if (myLive.isSelect()) {
                holder.mCheckBox.setImageResource(R.mipmap.ic_checked);
            } else {
                holder.mCheckBox.setImageResource(R.mipmap.ic_uncheck);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mCheckDocBean);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<CheckDocBean> myLiveList);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.radio_img)
        ImageView mRadioImg;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_source)
        TextView mTvSource;
        @BindView(R.id.root_view)
        RelativeLayout mRootView;
        @BindView(R.id.check_box)
        ImageView mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
