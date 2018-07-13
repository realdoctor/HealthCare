package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.InfoDetailModel;
import com.real.doctor.realdoc.model.ProductBean;

import org.w3c.dom.Text;

import java.util.List;


public class ImageAdapter extends RdBaseAdapter<InfoDetailModel>{
    public ImageAdapter(Context context, List list) {
        super(context, list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InfoDetailModel bean = getItem(position);
        final BrandHolder holder;
        if (convertView == null) {
            holder = new BrandHolder();
            convertView = mInflater.inflate(R.layout.activity_image_item, parent, false);
            holder.pImage=(ImageView) convertView.findViewById(R.id.img_show);
            holder.tv_content=(TextView)convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (BrandHolder) convertView.getTag();
        }
        holder.tv_content.setText(bean.content);
        Glide.with(mContext).load(bean.src).crossFade().error(R.drawable.timg).into((ImageView) holder.pImage);
        return convertView;
    }


    public class BrandHolder {
        private ImageView pImage;
        private TextView tv_content;
    }

}
