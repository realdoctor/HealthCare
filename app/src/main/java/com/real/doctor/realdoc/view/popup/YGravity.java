package com.real.doctor.realdoc.view.popup;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhujiabin on 2015/8/24.
 */

@IntDef({
        YGravity.CENTER,
        YGravity.ABOVE,
        YGravity.BELOW,
        YGravity.ALIGN_TOP,
        YGravity.ALIGN_BOTTOM,
})
@Retention(RetentionPolicy.SOURCE)
public @interface YGravity {
    int CENTER = 0;
    int ABOVE = 1;
    int BELOW = 2;
    int ALIGN_TOP = 3;
    int ALIGN_BOTTOM = 4;
}
