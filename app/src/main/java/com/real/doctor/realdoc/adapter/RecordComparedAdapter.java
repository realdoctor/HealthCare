package com.real.doctor.realdoc.adapter;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.RecordTableBean;

import java.util.List;

public class RecordComparedAdapter extends BaseMultiItemQuickAdapter<RecordTableBean, BaseViewHolder> {


    public RecordComparedAdapter(List<RecordTableBean> data) {
        super(data);
        //必须绑定type和layout的关系
        addItemType(RecordTableBean.HEADER_TYPE, R.layout.record_compare_item_header);
        addItemType(RecordTableBean.CONTENT_TYPE, R.layout.record_compare_item_content);
        addItemType(RecordTableBean.ADVICE_TYPE, R.layout.record_compare_item_advice);
    }

    @Override
    protected void convert(BaseViewHolder helper, RecordTableBean item) {
        switch (helper.getItemViewType()) {
            case RecordTableBean.HEADER_TYPE:
                break;
            case RecordTableBean.CONTENT_TYPE:
                helper.setText(R.id.content, item.getContent())
                        .setText(R.id.record_one, item.getFristContent())
                        .setText(R.id.record_two, item.getSecondContent());
                break;
            case RecordTableBean.ADVICE_TYPE:
                helper.setText(R.id.content, item.getContent())
                        .setText(R.id.record_one, item.getFristContent())
                        .setText(R.id.record_two, item.getSecondContent());
                break;

        }
    }
}
