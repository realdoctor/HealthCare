package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.BrandBean;
import java.util.List;


public class BrandAdapter extends RdBaseAdapter<BrandBean> {
    private int selectedPosition = -1;
    public BrandAdapter(Context context, List list) {
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
            holder.v_line=convertView.findViewById(R.id.v_line);
            holder.ll=convertView.findViewById(R.id.ll_tab);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        // 设置选中效果
        if(selectedPosition == position)
        {
            holder.v_line.setVisibility(View.VISIBLE);
            holder.brandText.setTextColor(mContext.getResources().getColor(R.color.main));

        } else {
            holder.v_line.setVisibility(View.INVISIBLE);
            holder.brandText.setTextColor(mContext.getResources().getColor(R.color.gray));
        }
        holder.brandText.setText(bean.breadName);
        return convertView;
    }

    public class BrandHolder {
        private TextView brandText;
        private View v_line;
        private LinearLayout ll;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }
}
