package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.service.RecordingService;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    //Recording controls
    @BindView(R.id.btnRecord)
    FloatingActionButton recordButton;
    @BindView(R.id.btnPause)
    Button pauseButton;
    @BindView(R.id.recording_status_text)
    TextView recordingPrompt;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    private int recordPromptCount = 0;

    private boolean startRecording = true;
    private boolean pauseRecording = true;
    private String mFolder;
    private String mModifyId;
    private String advice;
    private AddLabelBean addLabelBean;
    private Intent intent = null;
    private RecordManager instance = null;
    public static String RECORD_SERVICE = "android.intent.action.record.service";

    long timeWhenPaused = 0; //stores time when user clicks pause button
    private static final int REQUEST_ADD_ADVICE = 111;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(RecordActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        PackageManager p = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                p.checkPermission("android.permission.RECORD_AUDIO", "com.real.doctor.realdoc") && PackageManager.PERMISSION_GRANTED == p.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.real.doctor.realdoc"));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(RecordActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 0);
            }
        }
        pageTitle.setText("音频");
        Intent intent = getIntent();
        if (intent != null) {
            mFolder = intent.getStringExtra("folder");
            mModifyId = intent.getStringExtra("modifyId");
        }
        recordButton.setColorNormal(getResources().getColor(R.color.appthemecolor));
        recordButton.setColorPressed(getResources().getColor(R.color.search_bgcolor));
        pauseButton.setVisibility(View.GONE);
        instance = RecordManager.getInstance(RecordActivity.this);
        localBroadcast();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(RecordActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_SERVICE);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RecordBean bean = intent.getExtras().getParcelable("record");
                bean.setAdvice(advice);
                int spare;
                String name = "";
                if (EmptyUtils.isNotEmpty(addLabelBean)) {
                    name = addLabelBean.getName();
                }
                if (StringUtils.equals(name, "处方")) {
                    spare = 1;
                } else if (StringUtils.equals(name, "医嘱")) {
                    spare = 2;
                } else if (StringUtils.equals(name, "体征")) {
                    spare = 3;
                } else if (StringUtils.equals(name, "报告检查")) {
                    spare = 4;
                } else {
                    spare = 0;
                }
                bean.setSpareImage(spare);
                instance.insertRecord(RecordActivity.this, bean);
                Intent intentResult = new Intent();
                intentResult.putExtra("advice", advice);
                RecordActivity.this.setResult(RESULT_OK, intentResult);
                finish();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.btnRecord, R.id.btnPause, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                onRecord(startRecording);
                startRecording = !startRecording;
                break;
            case R.id.btnPause:
                onPauseRecord(pauseRecording);
                pauseRecording = !pauseRecording;
                break;
            case R.id.finish_back:
                finish();
                break;
        }
    }

    // Recording Start/Stop
    //TODO: recording pause
    private void onRecord(boolean start) {
        intent = new Intent(RecordActivity.this, RecordingService.class);
        if (start) {
            // start recording
            recordButton.setImageResource(R.mipmap.ic_media_stop);
            //start Chronometer
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (recordPromptCount == 0) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + ".");
                    } else if (recordPromptCount == 1) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + "..");
                    } else if (recordPromptCount == 2) {
                        recordingPrompt.setText(getString(R.string.record_in_progress) + "...");
                        recordPromptCount = -1;
                    }

                    recordPromptCount++;
                }
            });
            Bundle bundle = new Bundle();
            if (EmptyUtils.isNotEmpty(mModifyId)) {
                bundle.putString("modifyId", mModifyId);
            }
            bundle.putString("folder", mFolder);
            intent.putExtras(bundle);
            //start RecordingService
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            recordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            recordPromptCount++;

        } else {
            stopChangeBtn();
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.whether_to_audio_advice)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                    saveYesRecord();
                                }
                            }).

                            setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    arg0.dismiss();
                                    //stop recording
                                    stopRecording();
                                    Intent intentResult = new Intent();
                                    setResult(RESULT_OK, intentResult);
                                    finish();
                                }
                            }).setCancelable(false).show();
            // 在dialog执行show之后设置样式
            TextView tvMsg = (TextView) dialog.findViewById(android.R.id.message);
            tvMsg.setTextSize(16);
            tvMsg.setTextColor(Color.parseColor("#4E4E4E"));

            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextSize(16);
            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#8C8C8C"));
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextSize(16);
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1DA6DD"));
        }
    }

    private void saveYesRecord() {
        Intent startIntent = new Intent(RecordActivity.this, AddAdviceActivity.class);
        startIntent.putExtra("label", true);
        startActivityForResult(startIntent, REQUEST_ADD_ADVICE);
    }

    private void stopChangeBtn() {
        if (EmptyUtils.isNotEmpty(intent)) {
            recordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordingPrompt.setText(getString(R.string.record_prompt));
        }
    }

    private void stopRecording() {
        if (EmptyUtils.isNotEmpty(intent)) {
            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            intent = null;
        }
    }

    //TODO: implement pause recording
    private void onPauseRecord(boolean pause) {
        if (pause) {
            //pause recording
            pauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.mipmap.ic_media_play, 0, 0, 0);
            recordingPrompt.setText((String) getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
        } else {
            //resume recording
            pauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.mipmap.ic_media_pause, 0, 0, 0);
            recordingPrompt.setText((String) getString(R.string.pause_recording_button).toUpperCase());
            chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            chronometer.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_ADD_ADVICE) {
            advice = data.getStringExtra("advice");
            addLabelBean = data.getParcelableExtra("addLabelBean");
            stopRecording();
            intent = null;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
