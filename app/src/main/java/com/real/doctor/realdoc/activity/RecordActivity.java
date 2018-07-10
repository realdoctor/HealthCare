package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.service.RecordingService;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import java.io.File;

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
    private Intent intent = null;

    long timeWhenPaused = 0; //stores time when user clicks pause button

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
        }
        recordButton.setColorNormal(getResources().getColor(R.color.appthemecolor));
        recordButton.setColorPressed(getResources().getColor(R.color.search_bgcolor));
        pauseButton.setVisibility(View.GONE);
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
                Intent intent = new Intent();
                stopRecording();
                setResult(RESULT_OK, intent);
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
            bundle.putString("folder", mFolder);
            intent.putExtras(bundle);
            //start RecordingService
            startService(intent);
            //keep screen on while recording
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            recordingPrompt.setText(getString(R.string.record_in_progress) + ".");
            recordPromptCount++;

        } else {
            //stop recording
            stopRecording();
            intent = null;
        }
    }


    private void stopRecording() {
        if (EmptyUtils.isNotEmpty(intent)) {
            recordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordingPrompt.setText(getString(R.string.record_prompt));

            stopService(intent);
            //allow the screen to turn off again once recording is finished
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
    public void doBusiness(Context mContext) {

    }
}
