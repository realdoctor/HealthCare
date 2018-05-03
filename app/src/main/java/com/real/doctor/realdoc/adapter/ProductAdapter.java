package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.BrandBean;

import java.util.List;


public class ProductAdapter extends RdBaseAdapter<BrandBean> {
    private int selectedPosition = -1;
    public ProductAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BrandBean bean = getItem(position);
        final BrandHolder holder;
        if (convertView == null) {
            holder = new BrandHolder();
            convertView = mInflater.inflate(R.layout.brand_adapter_layout, parent, false);
            holder.brandText = convertView.findViewById(R.id.tv_brand);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        // 设置选中效果
        if(selectedPosition == position)
        {
            holder.brandText.setTextColor(Color.BLACK);

        } else {
            holder.brandText.setTextColor(Color.parseColor("#494949"));
        }
        holder.brandText.setText(bean.breadName);
        return convertView;
    }

    public class BrandHolder {
        private TextView brandText;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
