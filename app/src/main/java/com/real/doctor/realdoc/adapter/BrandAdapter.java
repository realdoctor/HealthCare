package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.BrandBean;
import java.util.List;


public class BrandAdapter extends RdBaseAdapter<BrandBean> {

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
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        holder.brandText.setText(bean.breadName);
        return convertView;
    }

    public class BrandHolder {
        private TextView brandText;
    }
}
