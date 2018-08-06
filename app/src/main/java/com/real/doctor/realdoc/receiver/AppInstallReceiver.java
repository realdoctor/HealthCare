package com.real.doctor.realdoc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        ToastUtil.showLong(context,"3333s");
        if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName.equals(DocUtils.getPackageName(context))) {
                //删除文件夹
                StringBuffer sb = new StringBuffer();
                sb.append(SDCardUtils.getGlobalDir());
                FileUtils.deleteDir(sb.toString());
                ToastUtil.showLong(context,"11111");
            }
        } else if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
//            Log.d(TAG, "package has been added");
            ToastUtil.showLong(context,"22222");
        }
    }
}
