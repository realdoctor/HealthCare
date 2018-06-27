package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.real.doctor.realdoc.widget.ZoomTutorial;

/**
 * @author:Jack zhujiabin
 * @tips :viewpager的适配器
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Bitmap[] newImgs;
    private Context mContext;
    private ZoomTutorial mZoomTutorial;

    public ViewPagerAdapter(Context context,Bitmap[] newImgs, ZoomTutorial zoomTutorial) {
        this.newImgs = newImgs;
        this.mContext = context;
        this.mZoomTutorial = zoomTutorial;
    }

    @Override
    public int getCount() {
        return newImgs.length;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {

        final ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(newImgs[position]);
        container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        imageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                mZoomTutorial.closeZoomAnim(position);
            }
        });
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}