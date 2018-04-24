package com.real.doctor.realdoc.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.real.doctor.realdoc.R;

/**
 * @author zhujiabin
 * @package com.real.doctor.rdsurvey.view
 * @fileName ${Name}
 * @Date 2018-2-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class SelectPopupWindow extends PopupWindow {

    private TextView mPhotoUpload;
    private TextView mSelectPhoto;
    private TextView mCancelText;
    private View mMenuView;
    private Activity mContext;

    public SelectPopupWindow(Activity context, OnClickListener itemsOnClick) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.select_popup_layout, null);
        mPhotoUpload = mMenuView.findViewById(R.id.photo_upload);
        mSelectPhoto = mMenuView.findViewById(R.id.select_photo);
        mCancelText = mMenuView.findViewById(R.id.cancel_text);
        //取消按钮
        mCancelText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //销毁弹出框
                backgroundAlpha(1);
                dismiss();
            }
        });
        //设置按钮监听
        mPhotoUpload.setOnClickListener(itemsOnClick);
        mSelectPhoto.setOnClickListener(itemsOnClick);
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        backgroundAlpha(1);
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        // 类型
        lp.alpha = bgAlpha; //0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }
}
