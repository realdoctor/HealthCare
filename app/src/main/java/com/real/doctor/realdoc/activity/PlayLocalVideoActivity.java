package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayLocalVideoActivity extends BaseActivity implements SurfaceHolder.Callback {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.center_start)
    ImageView centerStart;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.surfaceview)
    SurfaceView surfaceview;
    @BindView(R.id.image)
    ImageView image;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private MediaPlayer mediaPlayer;
    private String path;
    private int text = 0;
    private VideoBean videoBean;
    private boolean mIsPlay = false;//是否正在播放录像
    private android.os.Handler handler = new android.os.Handler();

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
        return R.layout.activity_play_local_video;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(PlayLocalVideoActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("视频播放");
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            videoBean = intent.getParcelableExtra("videoBean");
            path = videoBean.getFilePath();
            Bitmap bitmap = DocUtils.getVideoThumbnail(path);
            image.setImageBitmap(bitmap);
        }
        SurfaceHolder holder = surfaceview.getHolder();
        holder.addCallback(this);
        // setType必须设置，要不出错.
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void initEvent() {
    }

    @Override
    @OnClick({R.id.center_start, R.id.surfaceview, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.center_start:
                mIsPlay = true;
                image.setVisibility(View.GONE);
                centerStart.setVisibility(View.GONE);
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }
                mediaPlayer.reset();
                Uri uri = Uri.parse(path);
                mediaPlayer = MediaPlayer.create(PlayLocalVideoActivity.this, uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(mSurfaceHolder);
                try {
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                handler.postDelayed(runnable, 1000);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        centerStart.setVisibility(View.VISIBLE);
                        text = 0;
                        handler.removeCallbacks(runnable);
                    }
                });
                break;
            case R.id.surfaceview:
                if (mIsPlay) {
                    if (mediaPlayer != null) {
                        centerStart.setVisibility(View.VISIBLE);
                        mIsPlay = false;
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        time.setText(text + "");
                        text = 0;
                        handler.removeCallbacks(runnable);
                        mediaPlayer = null;
                    }
                }
                break;
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        surfaceview = null;
        mSurfaceHolder = null;
        handler.removeCallbacks(runnable);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
