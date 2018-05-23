package com.real.doctor.realdoc.view.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import com.real.doctor.realdoc.util.SizeUtils;

/**
 * Created by zhujiabin on 2017/4/7.
 */

public class ExcelMajorRecyclerView extends RecyclerView {

    public static final int CONST_FIVE = 5;
    private int lastX;
    private int lastY;

    public ExcelMajorRecyclerView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - lastX) - SizeUtils.dp2px(getContext(), CONST_FIVE) > Math.abs(y - lastY)) {
                    intercept = true;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
                break;
            default:
                intercept = super.onInterceptTouchEvent(ev);
                break;
        }
        lastX = x;
        lastY = y;
        return intercept;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }
}
