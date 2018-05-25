package com.real.doctor.realdoc.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CircleBtnView;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoActivity extends BaseActivity implements SurfaceHolder.Callback {

    private static final String TAG = "VideoActivity";
    @BindView(R.id.finish_back)
    ImageView finishBack;
    //@BindView(R.id.surface_view)
    SurfaceView surfaceView;
    @BindView(R.id.video_btn)
    CircleBtnView videoBtn;
    @BindView(R.id.time)
    TextView time;
    //是否正在录像
    private boolean startedFlg = false;
    private MediaRecorder recorder;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private String name;
    private String mFolder;
    private int text = 0;
    private VideoManager instance = null;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            text++;
            time.setText(text + "");
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        surfaceView = findViewById(R.id.surface_view);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            mFolder = intent.getStringExtra("folder");
        }
        instance = VideoManager.getInstance(this);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void initEvent() {
        videoBtn.setOnLongClickListener(new CircleBtnView.OnLongClickListener() {

            @Override
            public void onLongClick() {
                ToastUtil.showLong(VideoActivity.this, "开始录制视频!");

                if (!startedFlg) {
                    handler.postDelayed(runnable, 1000);
                    if (EmptyUtils.isEmpty(recorder)) {
                        recorder = new MediaRecorder();
                    }
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    if (camera != null) {
                        camera.setDisplayOrientation(90);
                        camera.unlock();
                        recorder.setCamera(camera);
                    }

                    try {
                        // 这两项需要放在setOutputFormat之前
                        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                        // Set output file format
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                        // 这两项需要放在setOutputFormat之后
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//                        recorder.setVideoSize(ScreenUtil.getScreenWidth(VideoActivity.this), ScreenUtil.getScreenHeight(VideoActivity.this));
//                        recorder.setVideoFrameRate(30);
                        recorder.setVideoEncodingBitRate(30 * 1024 * 1024);
                        recorder.setOrientationHint(90);
                        //设置记录会话的最大持续时间（毫秒）
                        recorder.setMaxDuration(30 * 1000);
                        recorder.setPreviewDisplay(mSurfaceHolder.getSurface());

                        path = SDCardUtils.getPictureDir();
                        if (path != null) {
                            File file = new File(path + File.separator + mFolder + File.separator + "movie" + File.separator);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            name = DateUtil.timeStamp() + ".mp4";
                            path = file + File.separator + name;
                            recorder.setOutputFile(path);
                            recorder.prepare();
                            recorder.start();
                            startedFlg = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNoMinRecord(int currentTime) {

            }

            @Override
            public void onRecordFinishedListener() {
                stopVideo();
            }
        });

    }

    private void stopVideo() {
        //stop
        if (startedFlg) {
            try {
                handler.removeCallbacks(runnable);
                recorder.setOnErrorListener(null);
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
                text = 0;
                //存储数据进数据库
                VideoBean bean = new VideoBean();
                bean.setFileName(name);
                bean.setFilePath(path);
                bean.setElapsedMillis(String.valueOf(time.getText()));
                bean.setFolder(mFolder);
                instance.insertVideo(VideoActivity.this, bean);
                ToastUtil.showLong(VideoActivity.this, "视频录制完成!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        startedFlg = false;
    }

    /**
     * 初始化摄像头
     *
     * @throws IOException
     */
    @SuppressLint("NewApi")
    private void initCamera() throws IOException {
        if (camera != null) {
            freeCameraResource();
        }
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (camera == null)
            return;

        // setCameraParams();
        camera.setDisplayOrientation(90);
        camera.setPreviewDisplay(mSurfaceHolder);
        camera.startPreview();
        camera.unlock();
    }

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.lock();
            camera.release();
            camera = null;
        }
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        try {
            initCamera();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceView = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
