package com.real.doctor.realdoc.app;

import android.app.Application;
import android.content.Context;

/**
 * @author zhujiabin
 * @package com.real.doctor.realdoc.app
 * @fileName ${Name}
 * @Date 2018-3-7 0007
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class RealDocApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
    }

    public static Context getmContext() {
        return mContext;
    }

}
