package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.UserFragment;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CircleImageView;
import com.real.doctor.realdoc.view.SelectPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class AccountActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.user_avator)
    CircleImageView userAvator;
    @BindView(R.id.identify)
    LinearLayout identify;
    @BindView(R.id.change_pwd)
    LinearLayout changePwd;
    @BindView(R.id.account_linear)
    LinearLayout accountLinear;
    @BindView(R.id.mobile)
    TextView mobile;
    //底部弹出菜单
    private SelectPopupWindow mPopup;
    private String mMobile;
    private String verifyFlag;
    private String mCurrentPhotoPath;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    //更新头像广播
    public static final String CHANGE_AVATOR = "android.intent.action.record.change.avator";

    @Override
    public int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AccountActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("账号设置");
    }

    @Override
    public void initData() {
        mPopup = new SelectPopupWindow(AccountActivity.this, itemsOnClick);
        mMobile = (String) SPUtils.get(AccountActivity.this, "mobile", "");
        mobile.setText(mMobile);
        //实名认证
        checkName();
    }

    @Override
    public void initEvent() {

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            backgroundAlpha(1);
            mPopup.dismiss();
            int i = v.getId();
            if (i == R.id.photo_upload) {//拍照上传
                requestPermission(new String[]{Manifest.permission.CAMERA}, 0x0002);
            } else if (i == R.id.select_photo) {//相册选择
                /**
                 * 读写SD卡
                 */
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
            }
        }
    };

    @Override
    @OnClick({R.id.identify, R.id.change_pwd, R.id.user_avator, R.id.finish_back})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.identify:
                //跳转到未实名认证界面
                if (StringUtils.equals(verifyFlag, "1")) {
                    ToastUtil.showLong(AccountActivity.this, "您已经实名认证!");
                } else {
                    intent = new Intent(AccountActivity.this, VerifyActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.change_pwd:
                intent = new Intent(AccountActivity.this, ChangePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.user_avator:
                //显示窗口
                mPopup.showAtLocation(accountLinear, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                backgroundAlpha(0.5f);
                mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //改变显示的按钮图片为正常状态
                        backgroundAlpha(1);
                    }
                });
                break;
            case R.id.finish_back:
                finish();
                break;
        }
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 类型
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
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
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .start(AccountActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    //------------------图片相关-----------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = new ArrayList<>();
        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            GlideUtils.loadImageView(this, photos.get(0), userAvator);
            //刷新需要刷新头像的界面
            Intent intent = new Intent(CHANGE_AVATOR);
            intent.putExtra("avator", photos.get(0));
            LocalBroadcastManager.getInstance(AccountActivity.this).sendBroadcast(intent);
            //通知后台头像已改
            uploadIcon(photos.get(0));
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            GlideUtils.loadImageView(this, mCurrentPhotoPath, userAvator);
            //刷新需要刷新头像的界面
            Intent intent = new Intent(CHANGE_AVATOR);
            intent.putExtra("avator", mCurrentPhotoPath);
            LocalBroadcastManager.getInstance(AccountActivity.this).sendBroadcast(intent);
            //通知后台头像已改
            uploadIcon(mCurrentPhotoPath);
        }
    }

    private void uploadIcon(String path) {
        if (NetworkUtil.isNetworkAvailable(AccountActivity.this)) {
            Map<String, RequestBody> maps = new HashMap<>();
//            maps.put("userId", DocUtils.toRequestBodyOfText(userId));
            File file = new File(String.valueOf(path));
            if (file.exists()) {
                RequestBody requestBody = DocUtils.toRequestBodyOfImage(file);
                maps.put("attach\"; filename=\"" + file.getName() + "", requestBody);//head_img图片key
            }

            HttpRequestClient.getInstance(AccountActivity.this).createBaseApi().uploads("upload/uploadImg/", maps, new BaseObserver<ResponseBody>(AccountActivity.this) {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                protected void onHandleSuccess(ResponseBody responseBody) {
                    //上传文件成功
                    String data = null;
                    try {
                        data = responseBody.string().toString();
                        try {
                            JSONObject object = new JSONObject(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(AccountActivity.this, e.getMessage());
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    public void takePhotoCompress() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(SDCardUtils.getSDCardPath(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            // 仅需改变这一行
            Uri fileUri = FileProvider7.getUriForFile(this, file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    private void checkName() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mMobile);
        HttpRequestClient.getInstance(AccountActivity.this).createBaseApi().get("user/certification/check"
                , param, new BaseObserver<ResponseBody>(AccountActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
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
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "verifyFlag")) {
                                        verifyFlag = obj.getString("verifyFlag");
                                    }

                                } else {
                                    ToastUtil.showLong(AccountActivity.this, "获取用户信息失败.请确定是否已登录!");
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

    @Override
    public void doBusiness(Context mContext) {

    }
}
