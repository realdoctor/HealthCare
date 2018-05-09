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

public class CheckCompareAdapter extends RecyclerView.Adapter<CheckCompareAdapter.ViewHolder> {

    private RecyclerView mRv;//实现单选方法三： RecyclerView另一种定向刷新方法：
    private static final int saveDocBean_MODE_CHECK = 0;
    int mEditMode = saveDocBean_MODE_CHECK;
    private Context context;
    private List<SaveDocBean> mSaveDocBean;
    //    private OnItemClickListener mOnItemClickListener;
    private List<Integer> selectedList;
    private int mSelectedPos = -1;

    public CheckCompareAdapter(Context context, RecyclerView rv) {
        this.context = context;
        mRv = rv;
    }

    public List<Integer> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<Integer> selectedList) {
        this.selectedList = selectedList;
    }

    public void notifyAdapter(List<SaveDocBean> saveDocBeanList, boolean isAdd) {
        if (!isAdd) {
            this.mSaveDocBean = saveDocBeanList;
        } else {
            this.mSaveDocBean.addAll(saveDocBeanList);
        }
        selectedList = new ArrayList<>();
        //实现单选方法二： 设置数据集时，找到默认选中的pos
        for (int i = 0; i < mSaveDocBean.size(); i++) {
            if (mSaveDocBean.get(i).getIsSelect()) {
                mSelectedPos = i;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_compare_item, parent, false);
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
        holder.mTvTime.setText(DateUtil.timeStamp2Date(saveDocBean.getTime(), "y年M月d日"));
        String mFolder = saveDocBean.getFolder();
        String mImg = saveDocBean.getImgs();
        if (EmptyUtils.isNotEmpty(mImg)) {
            holder.mRadioImg.setVisibility(View.VISIBLE);
            String[] imgs = mImg.split(";");
            GlideUtils.loadImageViewLoding(context, SDCardUtils.getPictureDir() + mFolder + File.separator + imgs[0], holder.mRadioImg, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        } else {
            holder.mRadioImg.setVisibility(View.GONE);
        }
        Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        holder.mCheckBox.setSelected(mSaveDocBean.get(position).getIsSelect());
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CouponVH couponVH = (CouponVH) mRv.findViewHolderForLayoutPosition(mSelectedPos);
                int removed = -1;
                int selectedItemPosition = holder.getLayoutPosition();
                if (mSaveDocBean.get(selectedItemPosition).getIsSelect()) {
                    mSaveDocBean.get(selectedItemPosition).setIsSelect(false);
                    holder.mCheckBox.setSelected(false);
                    int index = selectedList.indexOf(selectedItemPosition);
                    selectedList.remove(index);
                } else {
                    if (selectedList.size() == 2) {
                        removed = selectedList.remove(0);
                        mSaveDocBean.get(removed).setIsSelect(false);
                        holder.mCheckBox.setSelected(false);
                    }

                    mSaveDocBean.get(selectedItemPosition).setIsSelect(true);
                    holder.mCheckBox.setSelected(true);
                    selectedList.add(selectedItemPosition);
                }
                notifyDataSetChanged();
            }
        });

        if (mEditMode == saveDocBean_MODE_CHECK) {
            holder.mCheckBox.setVisibility(View.GONE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {         //实现单选方法三： RecyclerView另一种定向刷新方法：不会有白光一闪动画 也不会重复onBindVIewHolder
//                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mSaveDocBean);
//            }
//        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position, List<Object> payloads) {

        Log.d("TAG", "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "], payloads = [" + payloads + "]");
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle payload = (Bundle) payloads.get(0);
            if (payload.containsKey("KEY_BOOLEAN")) {
                boolean aBoolean = payload.getBoolean("KEY_BOOLEAN");
                holder.mCheckBox.setSelected(aBoolean);
            }
        }
    }
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.mOnItemClickListener = onItemClickListener;
//    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<SaveDocBean> saveDocBeanList);
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
        @BindView(R.id.tv_content)
        TextView mTvContent;
        @BindView(R.id.tv_time)
        TextView mTvTime;
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