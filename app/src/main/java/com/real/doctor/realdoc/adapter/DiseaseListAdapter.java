package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;

import java.util.List;

public class DiseaseListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Context context;

    public DiseaseListAdapter(Context context, int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, String item) {
        viewHolder.setText(R.id.disease, item);
    }

}
