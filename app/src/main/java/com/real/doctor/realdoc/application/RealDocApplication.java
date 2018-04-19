package com.real.doctor.realdoc.application;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

/**
 * @author zhujiabin
 * @package com.example.administrator.ruiyi.application
 * @fileName ${Name}
 * @Date 2018-1-4 0004
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public class RealDocApplication extends Application {
    private static final String TAG = "RealDocApplication";

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        StrictMode.setThreadPolicy(new
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new
                StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
    }


    public static Context getContext() {
        return mContext;
    }
}