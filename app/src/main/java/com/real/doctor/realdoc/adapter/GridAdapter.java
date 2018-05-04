package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
            convertView = mInflater.inflate(R.layout.grid_adapter_layout, parent, false);
            holder = new GridHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GridHolder) convertView.getTag();
        }
        final String url = bean.getImgUrl();
        int spare = bean.getSpareImage();
        String advice = bean.getAdvice();
        if (EmptyUtils.isEmpty(url) && spare != 0) {
            holder.mImg.setImageResource(spare);
            holder.mDelImg.setVisibility(View.GONE);
            holder.mAdvice.setText("");
        } else {
            Bitmap bitmap = ImageUtils.compressBitmapByPath(url.toString(), ScreenUtil.getScreenWidth(mContext), ScreenUtil.getScreenHeight(mContext));
            holder.mImg.setImageBitmap(bitmap);
            if (EmptyUtils.isNotEmpty(advice)) {
                holder.mAdvice.setText(advice);
            } else {
                holder.mAdvice.setText("");
            }
        }
        return convertView;
    }

    static class GridHolder {
        @BindView(R.id.grid_image)
        ImageView mImg;
        @BindView(R.id.delete_icon)
        ImageView mDelImg;
        @BindView(R.id.advice)
        TextView mAdvice;

        public GridHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
