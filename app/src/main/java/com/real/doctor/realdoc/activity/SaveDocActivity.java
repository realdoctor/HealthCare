package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.GridAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.bean.ImageBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.view.DocGridView;
import com.real.doctor.realdoc.view.SelectPopupWindow;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/23.
 */

public class SaveDocActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.ill)
    TextView ill;
    @BindView(R.id.hospital)
    EditText hospital;
    @BindView(R.id.doctor)
    EditText doctor;
    @BindView(R.id.doc_grid_view)
    DocGridView docGridView;
    @BindView(R.id.save_doc_layout)
    LinearLayout saveDocLayout;
    private List<ImageBean> imageList;
    private GridAdapter adapter;
    //底部弹出菜单
    private SelectPopupWindow mPopup;
    private String mCurrentPhotoPath = null;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;

    @Override
    public int getLayoutId() {
        return R.layout.activity_save_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        imageList = new ArrayList<>();
        adapter = new GridAdapter(this, imageList);
        docGridView.setAdapter(adapter);
        mPopup = new SelectPopupWindow(SaveDocActivity.this, itemsOnClick);
        ImageBean bean = new ImageBean();
        bean.setSpareImage(R.mipmap.add);
        imageList.add(bean);
    }

    @Override
    public void initEvent() {
        docGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (EmptyUtils.isNotEmpty(mCurrentPhotoPath)) {
            ImageBean bean = new ImageBean();
            bean.setSpareImage(R.mipmap.add);
            imageList.add(bean);
            mCurrentPhotoPath = null;
        }
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 获取点击的对象
        ImageBean imageBean = imageList.get(position);
        String url = imageBean.getImgUrl();
        int spare = imageBean.getSpareImage();
        if (url.equals("") && spare != 0) {
            //显示窗口
            mPopup.showAtLocation(saveDocLayout, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            backgroundAlpha(0.5f);
            mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //改变显示的按钮图片为正常状态
                    backgroundAlpha(1);
                }
            });
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            imageList.remove(imageList.size() - 1); // 现将加号移除（有加号才能显示此按钮）
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
                        .start(SaveDocActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            mCurrentPhotoPath = photos.get(0);
            ImageBean imageBean = new ImageBean();
            imageBean.setImgUrl(photos.get(0));
            imageList.add(imageBean);
            adapter.notifyDataSetChanged();
//            File file = new File(photos.get(0));
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            ImageBean imageBean = new ImageBean();
            imageBean.setImgUrl(mCurrentPhotoPath);
            imageList.add(imageBean);
            adapter.notifyDataSetChanged();
//            File file = new File(mCurrentPhotoPath);
        }
    }

    public void takePhotoCompress() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                    .format(new Date()) + ".png";
            File file = new File(SDCardUtils.getSDCardPath(), filename);
            mCurrentPhotoPath = file.getAbsolutePath();
            // 仅需改变这一行
            Uri fileUri = FileProvider7.getUriForFile(this, file);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }
}
