package com.real.doctor.realdoc.widget.clip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by zhujiabin on 2018/6/13.
 */

public class ClipImageLayout extends RelativeLayout {

    private ClipImageBorderView mClipImageBorderView;
    private ClipZoomImageView mClipZoomImageView;

    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mClipImageBorderView = new ClipImageBorderView(context);
        mClipZoomImageView = new ClipZoomImageView(context);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        addView(mClipZoomImageView, lp);
        addView(mClipImageBorderView, lp);

        initPaddingValue();
    }

    /**
     * 对外公布的设置边距的方法，单位dp
     *
     * @param mHorizontalPadding
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
        initPaddingValue();
    }

    /**
     * 计算padding的px
     */
    private void initPaddingValue() {
        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mHorizontalPadding, getResources().getDisplayMetrics());
        mClipZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageBorderView.setHorizontalPadding(mHorizontalPadding);
    }

    public void setImageDrawable(Drawable drawable) {
        mClipZoomImageView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mClipZoomImageView.setImageBitmap(bitmap);
    }

    /**
     * 截取图片
     *
     * @return
     */
    public Bitmap clip() {
        return mClipZoomImageView.clip();
    }
}
