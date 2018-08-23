package com.real.doctor.realdoc.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.service.UpdateService;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.ColorfulProgressbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/26.
 */

public class ProgressBarActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.color_progress_bar)
    ColorfulProgressbar colorProgressBar;
    private List<SaveDocBean> mList;
    private String doctorUserId;
    public static String HAVE_IMG = "android.intent.action.imgs";
    public static String HAVE_NOTHING = "android.intent.action.nothing";
    private String inquery;
    private String desease;
    private String questionId;
    private String orderNo;
    private boolean detail;
    private String patientRecordId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_progressbar;
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ProgressBarActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        finishBack.setVisibility(View.GONE);
        pageTitle.setText("病历资料上传");
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
            inquery = intent.getExtras().getString("inquery");
            desease = intent.getExtras().getString("desease");
            mList = intent.getParcelableArrayListExtra("mList");
            orderNo = intent.getExtras().getString("orderNo");
            doctorUserId = intent.getExtras().getString("doctorUserId");
            detail = getIntent().getBooleanExtra("detail", false);
            questionId = intent.getExtras().getString("questionId");
            patientRecordId = intent.getExtras().getString("patientRecordId");
        }
        localBroadcast();
        startService();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HAVE_IMG);
        intentFilter.addAction(HAVE_NOTHING);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String path = intent.getExtras().getString("folderName");
                if (StringUtils.equals(action, HAVE_IMG)) {
                    ToastUtil.showLong(ProgressBarActivity.this, "病历资源打包成功!");
                } else if (StringUtils.equals(action, HAVE_NOTHING)) {
                    ToastUtil.showLong(ProgressBarActivity.this, "没有病历图片,音频,视频资源可打包,但病历信息已经上传完成!");
                }
                Intent extras = new Intent(ProgressBarActivity.this, MyRevisitActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                extras.putExtra("doctorUserId", doctorUserId);
                extras.putExtra("questionId", questionId);
                startActivity(extras);
                finish();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            Intent startServiceIntent = new Intent(this, UpdateService.class);
            startServiceIntent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
            startServiceIntent.putExtra("desease", desease);
            startServiceIntent.putExtra("inquery", inquery);
            startServiceIntent.putExtra("doctorUserId", doctorUserId);
            startServiceIntent.putExtra("orderNo", orderNo);
            startServiceIntent.putExtra("questionId", questionId);
            startServiceIntent.putExtra("patientRecordId", patientRecordId);
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
