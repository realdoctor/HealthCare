package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.DeptBean;

import java.util.List;


public class LeftAdapter extends RdBaseAdapter<DeptBean> {
    private int selectedPosition = -1;

    public LeftAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeptBean bean = getItem(position);
        final Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = mInflater.inflate(R.layout.brand_adapter_layout, parent, false);
            holder.brandText = convertView.findViewById(R.id.tv_brand);
            holder.v_line = convertView.findViewById(R.id.v_line);
            holder.ll = convertView.findViewById(R.id.ll_tab);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        // 设置选中效果
        if (selectedPosition == position) {
            holder.v_line.setVisibility(View.VISIBLE);
            holder.ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.v_line.setVisibility(View.INVISIBLE);
            holder.ll.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        holder.brandText.setText(bean.deptName);
        return convertView;
    }

    public class Holder {
        private TextView brandText;
        private View v_line;
        private LinearLayout ll;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
