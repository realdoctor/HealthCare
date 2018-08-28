package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
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

import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.scanner.ScannerFinderView;
import com.real.doctor.realdoc.view.scanner.camera.CameraManager;
import com.real.doctor.realdoc.view.scanner.decode.CaptureActivityHandler;
import com.real.doctor.realdoc.view.scanner.decode.DecodeManager;
import com.real.doctor.realdoc.view.scanner.decode.InactivityTimer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

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
    private String userId;
    private String token;

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
        userId = (String) SPUtils.get(ScannerActivity.this, Constants.USER_KEY, "");
        token = (String) SPUtils.get(ScannerActivity.this, Constants.TOKEN, "");
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


    private void qrSucceed(final String result) {
        focusDoctor(result);
    }

    private void focusDoctor(final String doctorId) {
        JSONObject json = new JSONObject();
        try {
            json.put("doctorId", doctorId);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(ScannerActivity.this).createBaseApi().json("user/mydoctor/add/"
                , body, new BaseObserver<ResponseBody>(ScannerActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        //do nothing 通知后台开始计时失败
                        Log.d(TAG, e.getMessage());
                        goToLogin();
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = "";
                        String code = "";
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    ToastUtil.showLong(ScannerActivity.this, "关注成功");
                                    Intent intent = new Intent(ScannerActivity.this, DoctorsDetailActivity.class);
                                    String[] doctorUserId = doctorId.split("=");
                                    intent.putExtra("doctorUserId", doctorUserId[1]);
                                    startActivity(intent);
                                    ScannerActivity.this.finish();
                                } else {
                                    ToastUtil.showLong(ScannerActivity.this, msg);
                                    goToLogin();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    private void goToLogin() {
        if (EmptyUtils.isEmpty(token)) {
            Intent intent = new Intent(ScannerActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
