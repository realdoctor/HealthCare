package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDeleteAdapter extends BaseQuickAdapter<SaveDocBean, BaseViewHolder> {

    private Context context;
    private List<SaveDocBean> data;
    private SaveDocManager instance;

    public DocDeleteAdapter(Context context, @LayoutRes int layoutResId, List<SaveDocBean> data) {
        super(layoutResId, data);
        this.data = data;
        this.context = context;
        instance = SaveDocManager.getInstance(context);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final SaveDocBean item) {
        viewHolder.setText(R.id.doc_detail_title, item.getIll())
                .setText(R.id.doc_detail_content, item.getHospital())
                .setText(R.id.doc_detail_time, DateUtil.timeStamp2Date(item.getTime(), "yyyy年MM月dd日 HH:mm"));
        Button rvDeleteBtn = viewHolder.getView(R.id.rv_delete_btn);
        final int pos = viewHolder.getLayoutPosition();// 获取当前item的position
        rvDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除item,数据库中的isSelect=false
                item.setIsSelect(false);
                instance.updateRecord(item);
                data.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }
}
