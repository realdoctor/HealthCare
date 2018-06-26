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
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.model.ProductInfo;

import java.util.List;


public class ProductAdapter extends RdBaseAdapter<ProductBean> implements  View.OnClickListener{
    private ClickListener mListener;
    public ProductAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductBean bean = getItem(position);
        final BrandHolder holder;
        if (convertView == null) {
            holder = new BrandHolder();
            convertView = mInflater.inflate(R.layout.product_detail_item, parent, false);
            holder.pTitle = (TextView) convertView.findViewById(R.id.product_detail_title);
            holder.pContent=(TextView) convertView.findViewById(R.id.product_detail_content);
            holder.pImage=(ImageView) convertView.findViewById(R.id.product_detail_img);
            holder.product_price=(TextView) convertView.findViewById(R.id.product_price);
            holder.img_buy=(ImageView)convertView.findViewById(R.id.img_buy);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        holder.pTitle.setText(bean.getName());
        holder.pContent.setText(bean.getGoodsDescription());
        holder.product_price.setText("￥"+bean.getCost());
        holder.img_buy.setOnClickListener(this);
        holder.img_buy.setTag(bean);
        Glide.with(mContext).load(bean.getSmallPic()).crossFade().into((ImageView) holder.pImage);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        mListener.clickListener(view);
    }

    public class BrandHolder {
        private ImageView pImage;
        private TextView pTitle;
        private TextView pContent;
        private TextView product_price;
        private ImageView img_buy;
    }
    //自定义接口，用于回调按钮点击事件到Activity
    public interface ClickListener{
        public void clickListener(View v);
    }

    public ClickListener getmListener() {
        return mListener;
    }

    public void setmListener(ClickListener mListener) {
        this.mListener = mListener;
    }
}
