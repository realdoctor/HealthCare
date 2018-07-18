
package com.real.doctor.realdoc.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.service.GlobeUnzipService;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.view.ColorfulProgressbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/26.
 */

public class GlobeUnzipActivity extends BaseActivity {

    @BindView(R.id.color_progress_bar)
    ColorfulProgressbar colorProgressBar;
    public static String IS_UNZIP = "android.intent.is.unzip";
    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_globe_unzip;
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        colorProgressBar.setHeight(SizeUtils.dip2px(this, 20));
        colorProgressBar.setProgress(100);
        colorProgressBar.setSecondProgress(100);
        colorProgressBar.setAnimation(true);
        colorProgressBar.showPercentText(false);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            url = intent.getExtras().getString("url");
        }
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                if (StringUtils.equals(action, HAVE_IMG)) {
//                    ToastUtil.showLong(GlobeUnzipActivity.this, "病历资源打包成功!");
//                } else if (StringUtils.equals(action, HAVE_NOTHING)) {
//                    ToastUtil.showLong(GlobeUnzipActivity.this, "没有病历图片,音频,视频资源可打包,但病历信息已经上传完成!");
//                }
//                Intent extras = new Intent(GlobeUnzipActivity.this, CheckDetailActivity.class);
//                extras.putExtra("path", path);
//                extras.putExtra("questionId", questionId);
//                extras.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
//                extras.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(extras);
                finish();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //启动服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                Intent startServiceIntent = new Intent(GlobeUnzipActivity.this, GlobeUnzipService.class);
                startServiceIntent.putExtra("url", url);
                startService(startServiceIntent);
                JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), GlobeUnzipService.class.getName()))
                        .setPeriodic(2000)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
                jobScheduler.schedule(jobInfo);
            }
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
