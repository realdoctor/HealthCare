package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.ImagePagerView;

import java.util.List;

/**
 * @author zhujiabin
 * @package com.real.doctor.rdsurvey.adapter
 * @fileName ${Name}
 * @Date 2018-2-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class GridAdapter extends RdBaseAdapter<ImageBean> {

    public GridAdapter(Context context, List list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageBean bean = getItem(position);
        final GridHolder holder;
        if (convertView == null) {
            holder = new GridHolder();
            convertView = mInflater.inflate(R.layout.grid_adapter_layout, parent, false);
            holder.mImg = convertView.findViewById(R.id.grid_image);
            holder.mDelImg = convertView.findViewById(R.id.delete_icon);
            convertView.setTag(holder);
        } else {
            holder = (GridHolder) convertView.getTag();
        }
        final String url = bean.getImgUrl();
        int spare = bean.getSpareImage();
        if (url.equals("") && spare != 0) {
            holder.mImg.setImageResource(spare);
            holder.mDelImg.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = ImageUtils.compressBitmapByPath(url.toString(), ScreenUtil.getScreenWidth(mContext), ScreenUtil.getScreenHeight(mContext));
            holder.mImg.setImageBitmap(bitmap);
        }

//        holder.mImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ToastUtil.showLong(mContext, "你点击了一张图片!");
//            }
//        });

        return convertView;
    }

    public class GridHolder {
        private ImageView mImg;
        private ImageView mDelImg;
    }
}
