
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.service.GlobeUnzipService;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.view.ColorfulProgressbar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.real.doctor.realdoc.activity.RecordListActivity.RECORD_LIST_TEXT;

/**
 * Created by Administrator on 2018/4/26.
 */

public class GlobeUnzipActivity extends BaseActivity {

    @BindView(R.id.color_progress_bar)
    ColorfulProgressbar colorProgressBar;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    public static String IS_UNZIP = "android.intent.is.unzip";
    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_globe_unzip;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(GlobeUnzipActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("从服务端病历下载");
        finishBack.setVisibility(View.GONE);
        colorProgressBar.setHeight(SizeUtils.dip2px(this, 20));
        colorProgressBar.setProgress(100);
        colorProgressBar.setSecondProgress(100);
        colorProgressBar.setAnimation(true);
        colorProgressBar.showPercentText(false);
    }

    @Override
    public void initData() {
        url = (String) SPUtils.get(GlobeUnzipActivity.this, Constants.URL, "");
        localBroadcast();
        startService();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IS_UNZIP);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //通知页面刷新数据(两处页面刷新,精度条finish)
                Intent refreshIntent = new Intent(RECORD_LIST_TEXT);
                LocalBroadcastManager.getInstance(GlobeUnzipActivity.this).sendBroadcast(refreshIntent);
                finish();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void startService() {
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
