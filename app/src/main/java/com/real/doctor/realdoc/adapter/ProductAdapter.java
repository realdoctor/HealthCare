package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.ProductInfo;

import java.util.List;


public class ProductAdapter extends RdBaseAdapter<ProductInfo> {
    public ProductAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductInfo bean = getItem(position);
        final BrandHolder holder;
        if (convertView == null) {
            holder = new BrandHolder();
            convertView = mInflater.inflate(R.layout.product_detail_item, parent, false);
            holder.pTitle = (TextView) convertView.findViewById(R.id.product_detail_title);
            holder.pContent=(TextView) convertView.findViewById(R.id.product_detail_content);
            holder.pImage=(ImageView) convertView.findViewById(R.id.product_detail_img);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        holder.pTitle.setText(bean.getName());
        holder.pContent.setText(bean.getDesc());
        Glide.with(mContext).load(bean.getImageUrl()).crossFade().into((ImageView) holder.pImage);
        return convertView;
    }

    public class BrandHolder {
        private ImageView pImage;
        private TextView pTitle;
        private TextView pContent;
    }
}
