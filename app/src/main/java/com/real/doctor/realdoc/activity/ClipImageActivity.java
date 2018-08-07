package com.real.doctor.realdoc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.widget.clip.ClipImageLayout;

import java.io.FileInputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClipImageActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.right_btn)
    TextView right_btn;
    @BindView(R.id.clip_image_layout)
    ClipImageLayout clipImageLayout;
    public static final String RESULT_PATH = "crop_image";
    private static final String KEY = "path";
    private String mMobile;

    public static void startActivity(Activity activity, String path, int code) {
        Intent intent = new Intent(activity, ClipImageActivity.class);
        intent.putExtra(KEY, path);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_clip_image;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ClipImageActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("头像裁剪");
    }

    @Override
    public void initData() {
        mMobile = (String) SPUtils.get(ClipImageActivity.this, Constants.MOBILE, "");
        String path = getIntent().getStringExtra(KEY);
        // 有的系统返回的图片是旋转了，有的没有旋转，所以处理
        int degreee = readBitmapDegree(path);
        Bitmap bitmap = createBitmap(path);
        if (bitmap != null) {
            if (degreee == 0) {
                clipImageLayout.setImageBitmap(bitmap);
            } else {
                clipImageLayout.setImageBitmap(rotateBitmap(degreee, bitmap));
            }
        } else {
            finish();
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_btn:
                Bitmap bitmap = clipImageLayout.clip();
                StringBuffer sb = new StringBuffer();
                sb.append(SDCardUtils.getGlobalDir());
                sb.append("avater");
                sb.append(mMobile);
                sb.append(".png");
                String path = sb.toString();
                boolean isSaveImg = ImageUtils.save(bitmap, path, Bitmap.CompressFormat.PNG, true);
                if (isSaveImg) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_PATH, path);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    /**
     * 创建图片
     *
     * @param path
     * @return
     */
    private Bitmap createBitmap(String path) {
        if (path == null) {
            return null;
        }

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        FileInputStream is = null;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(path);
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    // 读取图像的旋转度
    private int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    // 旋转图片
    private Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return resizedBitmap;
    }
}
