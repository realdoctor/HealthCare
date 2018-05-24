package com.real.doctor.realdoc.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.MySharedPreferences;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Daniel on 12/28/2014.
 */
public class RecordingService extends Service {

    private static final String LOG_TAG = "RecordingService";

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;
    private RecordManager instance = null;
    private String mFolder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = RecordManager.getInstance(RecordingService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mFolder = intent.getStringExtra("folder");
        startRecording();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null) {
            stopRecording();
        }

        super.onDestroy();
    }

    public void startRecording() {
        mStartingTimeMillis = Long.valueOf(DateUtil.timeStamp());

        setFileNameAndPath();
        if (EmptyUtils.isEmpty(mRecorder)) {
            startRecord();
        } else {
            mRecorder.stop();
            mRecorder.release();
            startRecord();
        }
    }

    public void startRecord() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            mRecorder.prepare();
            mRecorder.start();
            //startTimer();
            //startForeground(1, createNotification());

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void setFileNameAndPath() {
        mFileName = mStartingTimeMillis + ".mp4";
        mFilePath = SDCardUtils.getSDCardPath() + "RealDoc" + File.separator + mFolder + File.separator + "music" + File.separator;
        //创建路径
        File folderFile = new File(mFilePath);
        if (!folderFile.exists())
            folderFile.mkdirs();
        mFilePath += mFileName;
    }

    public void stopRecording() {
        if (EmptyUtils.isNotEmpty(mRecorder)) {
            mRecorder.setOnErrorListener(null);
            mRecorder.setOnInfoListener(null);
            mRecorder.setPreviewDisplay(null);
            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO 如果当前java状态和jni里面的状态不一致，
                //e.printStackTrace();
                mRecorder = null;
                mRecorder = new MediaRecorder();
            }
            mRecorder.release();
            mRecorder = null;
        }
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        ToastUtil.showLong(RecordingService.this, "录音完成!");
        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;

        try {
            RecordBean bean = new RecordBean();
            bean.setFileName(mFileName);
            bean.setFilePath(mFilePath);
            bean.setElapsedMillis(String.valueOf(mElapsedMillis));
            bean.setFolder(mFolder);
            instance.insertRecord(this, bean);
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception", e);
        }
    }

}
