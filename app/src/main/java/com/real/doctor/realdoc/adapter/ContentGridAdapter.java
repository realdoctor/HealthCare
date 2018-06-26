package com.real.doctor.realdoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.view.DocContentDialog;
import com.real.doctor.realdoc.widget.ZoomTutorial;

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

public class ContentGridAdapter extends RdBaseAdapter<ImageBean> {
    private Bitmap[] newImgs;
    private DocContentDialog dialog;

    public ContentGridAdapter(Context context, List list, Bitmap[] newImgs) {
        super(context, list);
        this.newImgs = newImgs;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
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
        final String advice = bean.getAdvice();
        if (EmptyUtils.isEmpty(url) && spare != 0) {
            holder.mImg.setImageResource(spare);
            holder.mDelImg.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = ImageUtils.compressBitmapByPath(url.toString(), ScreenUtil.getScreenWidth(mContext), ScreenUtil.getScreenHeight(mContext));
            holder.mImg.setImageBitmap(bitmap);
            holder.mAdvice.setText(advice);
        }
        holder.mImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //当前drawable的res的id
                setViewPagerAndZoom(holder.mImg, position);
            }
        });
        holder.mAdvice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框界面
                dialog = new DocContentDialog(mContext,advice).builder()
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(true)
                        .setConfirmBtn(new DocContentDialog.ConfirmListener() {
                            @Override
                            public void onConfirmClick() {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        return convertView;
    }

    public void setViewPagerAndZoom(View v, int position) {
        //得到要放大展示的视图界面
        ViewPager expandedView = (ViewPager) ((Activity) mContext).findViewById(R.id.detail_view);
        //最外层的容器，用来计算
        View containerView = (FrameLayout) ((Activity) mContext).findViewById(R.id.container_iv);
        //实现放大缩小类，传入当前的容器和要放大展示的对象
        ZoomTutorial mZoomTutorial = new ZoomTutorial(containerView, expandedView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(mContext,
                newImgs, mZoomTutorial);
        expandedView.setAdapter(adapter);
        expandedView.setCurrentItem(position);

        // 通过传入Id来从小图片扩展到大图，开始执行动画
        mZoomTutorial.zoomImageFromThumb(v);
        mZoomTutorial.setOnZoomListener(new ZoomTutorial.OnZoomListener() {

            @Override
            public void onThumbed() {
                // TODO 自动生成的方法存根
                System.out.println("现在是-------------------> 小图状态");
            }

            @Override
            public void onExpanded() {
                // TODO 自动生成的方法存根
                System.out.println("现在是-------------------> 大图状态");
            }
        });
    }

    public class GridHolder {
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
