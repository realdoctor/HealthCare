package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ProductBean;

import org.w3c.dom.Text;

import java.util.List;


public class OrderProductAdapter extends RdBaseAdapter<ProductBean>{
    public OrderProductAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductBean bean = getItem(position);
        final BrandHolder holder;
        if (convertView == null) {
            holder = new BrandHolder();
            convertView = mInflater.inflate(R.layout.order_product_detail_item, parent, false);
            holder.pTitle = (TextView) convertView.findViewById(R.id.product_detail_title);
            holder.pContent=(TextView) convertView.findViewById(R.id.product_detail_content);
            holder.pImage=(ImageView) convertView.findViewById(R.id.product_detail_img);
            holder.product_price=(TextView) convertView.findViewById(R.id.product_price);
            holder.tv_num=(TextView)convertView.findViewById(R.id.tv_num);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        holder.pTitle.setText(bean.getName());
        holder.pContent.setText(bean.getGoodsDescription());
        holder.product_price.setText("￥"+bean.getCost());
        holder.tv_num.setText("x "+bean.getNum());
        Glide.with(mContext).load(bean.getSmallPic()).crossFade().into((ImageView) holder.pImage);
        return convertView;
    }


    public class BrandHolder {
        private ImageView pImage;
        private TextView pTitle;
        private TextView pContent;
        private TextView product_price;
        private TextView tv_num;
    }
}
