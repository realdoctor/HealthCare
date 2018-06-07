package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;

import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.view.scanner.ScannerFinderView;
import com.real.doctor.realdoc.view.scanner.camera.CameraManager;
import com.real.doctor.realdoc.view.scanner.decode.CaptureActivityHandler;
import com.real.doctor.realdoc.view.scanner.decode.DecodeManager;
import com.real.doctor.realdoc.view.scanner.decode.InactivityTimer;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScannerActivity extends BaseActivity implements SurfaceHolder.Callback {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    private CaptureActivityHandler captureActivityHandler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.qr_code_view)
    ScannerFinderView qrCodeFinderView;
    @BindView(R.id.qr_code_view_stub)
    ViewStub surfaceViewStub;
    private SurfaceView surfaceView;
    private DecodeManager decodeManager = new DecodeManager();
    @BindView(R.id.light_switch)
    Switch lightSwitch;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scanner;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ScannerActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        inactivityTimer = new InactivityTimer(this);
        hasSurface = false;
        pageTitle.setText("扫一扫");
    }

    @Override
    public void initEvent() {
        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CameraManager.get().setFlashLight(isChecked);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }


    public Rect getCropRect() {
        return qrCodeFinderView.getRect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission(new String[]{Manifest.permission.CAMERA}, 0x0001);
    }

    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
                CameraManager.init();
                initCamera();
                break;
        }
    }

    private void initCamera() {
        if (null == surfaceView) {
            surfaceViewStub.setLayoutResource(R.layout.surface_view_layout);
            surfaceView = (SurfaceView) surfaceViewStub.inflate();
        }
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (captureActivityHandler != null) {
            try {
                captureActivityHandler.quitSynchronously();
                captureActivityHandler = null;
                hasSurface = false;
                if (null != surfaceView) {
                    surfaceView.getHolder().removeCallback(this);
                }
                CameraManager.get().closeDriver();
            } catch (Exception e) {
                // 关闭摄像头失败的情况下,最好退出该Activity,否则下次初始化的时候会显示摄像头已占用.
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (null != inactivityTimer) {
            inactivityTimer.shutdown();
        }
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     */
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        if (null == result) {
            decodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            handleResult(result);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            if (!CameraManager.get().openDriver(surfaceHolder)) {
                return;
            }
        } catch (IOException e) {
            // 基本不会出现相机不存在的情况
            Toast.makeText(this, getString(R.string.camera_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return;
        } catch (RuntimeException re) {
            re.printStackTrace();
            return;
        }
        qrCodeFinderView.setVisibility(View.VISIBLE);
        if (captureActivityHandler == null) {
            captureActivityHandler = new CaptureActivityHandler(this);
        }
    }

    public void restartPreview() {
        if (null != captureActivityHandler) {
            try {
                captureActivityHandler.restartPreviewAndDecode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getCaptureActivityHandler() {
        return captureActivityHandler;
    }

    private void handleResult(Result result) {
        if (TextUtils.isEmpty(result.getText())) {
            decodeManager.showCouldNotReadQrCodeFromScanner(this, new DecodeManager.OnRefreshCameraListener() {
                @Override
                public void refresh() {
                    restartPreview();
                }
            });
        } else {
            Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(200L);
            qrSucceed(result.getText());
        }
    }


    private void qrSucceed(String result) {
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.notification)
                .setMessage(result)
                .setPositiveButton(R.string.positive_button_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        restartPreview();
                    }
                })
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                restartPreview();
            }
        });
    }

}
