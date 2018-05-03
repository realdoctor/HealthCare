package com.real.doctor.realdoc.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import android.os.Parcelable;
import android.view.View;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.service.UpdateService;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.view.ColorfulProgressbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/26.
 */

public class ProgressBarActivity extends BaseActivity {

    @BindView(R.id.color_progress_bar)
    ColorfulProgressbar colorProgressBar;
    private List<SaveDocBean> mList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_progressbar;
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
        if (intent != null) {
            mList = intent.getParcelableArrayListExtra("mList");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            Intent startServiceIntent = new Intent(this, UpdateService.class);
            startServiceIntent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
            startService(startServiceIntent);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), UpdateService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
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
