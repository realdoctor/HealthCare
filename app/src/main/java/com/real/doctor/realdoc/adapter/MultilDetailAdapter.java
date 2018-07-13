package com.real.doctor.realdoc.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;

import java.util.List;

public class MultilDetailAdapter extends BaseMultiItemQuickAdapter<SaveDocBean, BaseViewHolder> {

    public MultilDetailAdapter(List<SaveDocBean> data) {
        super(data);
        //必须绑定type和layout的关系
        addItemType(SaveDocBean.TYPE_ONE, R.layout.doc_detail_item);
        addItemType(SaveDocBean.TYPE_TWO, R.layout.check_doc_title_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, SaveDocBean item) {
        switch (helper.getItemViewType()) {
            case SaveDocBean.TYPE_ONE:
                helper.setText(R.id.doc_detail_title, item.getIll())
                        .setText(R.id.doc_detail_content, item.getHospital())
                        .setText(R.id.doc_detail_time, DateUtil.timeStamp2Date(item.getTime(), "yyyy年MM月dd日"));
                break;
            case SaveDocBean.TYPE_TWO:
                //do nothing
                break;
        }
    }
}
