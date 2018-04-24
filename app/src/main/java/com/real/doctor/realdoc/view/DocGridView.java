package com.real.doctor.realdoc.view;

/**
 * Created by Administrator on 2018/4/23.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class DocGridView extends GridView {

    public DocGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public DocGridView(Context context) {
        super(context);
    }

    public DocGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}