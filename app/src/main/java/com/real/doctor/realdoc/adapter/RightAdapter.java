package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DeptBean;
import com.real.doctor.realdoc.util.EmptyUtils;

import java.util.List;


public class RightAdapter extends BaseQuickAdapter<DeptBean, BaseViewHolder> {

    public RightAdapter(int layoutResId, @Nullable List<DeptBean> data) {
        super(layoutResId, data);
    }

    protected void convert(BaseViewHolder viewHolder, DeptBean item) {
        TextView textView = viewHolder.getView(R.id.tv_show);
        textView.setText(item.deptName);
    }
}