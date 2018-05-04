package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/27.
 */

public class DocCompareActivity extends BaseActivity {

    @BindView(R.id.image_one)
    ImageView imageOne;
    @BindView(R.id.image_two)
    ImageView imageTwo;
    @BindView(R.id.ill)
    TextView ill;
    @BindView(R.id.hospital)
    TextView hospital;
    @BindView(R.id.doctor)
    TextView doctor;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private List<SaveDocBean> mList;
    private String[] imgOne;
    private String[] imgTwo;
    private String folderOne;
    private String folderTwo;
    private List<String> mListOne;
    private List<String> mListTwo;
    private boolean mOneFlag = false;
    private boolean mTwoFlag = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            mList = getIntent().getParcelableArrayListExtra("mSelectList");
            if (mList.size() > 0 && mList.size() == 2) {
                String illOne = mList.get(0).getIll();
                String doctorOne = mList.get(0).getDoctor();
                folderOne = mList.get(0).getFolder();
                String timeOne = mList.get(0).getTime();
                String imgsOne = mList.get(0).getImgs();
                imgOne = imgsOne.split(";");

                if (EmptyUtils.isNotEmpty(folderOne)) {
                    String pathOne = SDCardUtils.getPictureDir() + folderOne + File.separator + imgOne[0];
                    GlideUtils.loadImageViewLoding(DocCompareActivity.this, pathOne, imageOne, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                }
                String illTwo = mList.get(1).getIll();
                String doctorTwo = mList.get(1).getDoctor();
                folderTwo = mList.get(1).getFolder();
                String timeTwo = mList.get(1).getTime();
                String imgsTwo = mList.get(1).getImgs();
                imgTwo = imgsTwo.split(";");
                if (EmptyUtils.isNotEmpty(folderTwo)) {
                    String pathTwo = SDCardUtils.getPictureDir() + folderTwo + File.separator + imgTwo[0];
                    GlideUtils.loadImageViewLoding(DocCompareActivity.this, pathTwo, imageTwo, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                }
                //填入其他字段
                if (EmptyUtils.isNotEmpty(illOne) && EmptyUtils.isNotEmpty(illTwo) && StringUtils.equals(illOne, illTwo)) {
                    ill.setText(illOne);
                    if (EmptyUtils.isNotEmpty(doctorOne)) {
                        hospital.setText("1." + doctorOne);
                    }
                    if (EmptyUtils.isNotEmpty(doctorTwo)) {
                        hospital.setText("2." + doctorTwo);
                    }
                    if (EmptyUtils.isNotEmpty(doctorOne) && EmptyUtils.isNotEmpty(doctorTwo)) {
                        hospital.setText("1." + doctorOne + "\n" + "2." + doctorTwo);
                    }
                    if (EmptyUtils.isNotEmpty(timeOne)) {
                        time.setText("1." + timeOne);
                    }
                    if (EmptyUtils.isNotEmpty(timeTwo)) {
                        time.setText("2." + timeTwo);
                    }
                    if (EmptyUtils.isNotEmpty(timeOne) && EmptyUtils.isNotEmpty(timeTwo)) {
                        time.setText("1." + timeOne + "\n" + "2." + timeTwo);
                    }
                } else {
                    //该情况不会发生
                    ToastUtil.showLong(DocCompareActivity.this, "疾病不相同，不能进行比较!");
                    return;
                }
            }
        }
    }

    @Override
    public void initData() {
        mListOne = new ArrayList<>();
        mListTwo = new ArrayList<>();
        if (EmptyUtils.isNotEmpty(folderOne)) {
            for (int i = 0; i < imgOne.length; i++) {
                String path = SDCardUtils.getPictureDir() + folderOne + File.separator + imgOne[i];
                mListOne.add(path);
            }
        }
        if (EmptyUtils.isNotEmpty(folderTwo)) {
            for (int i = 0; i < imgTwo.length; i++) {
                String path = SDCardUtils.getPictureDir() + folderTwo + File.separator + imgTwo[i];
                mListTwo.add(path);
            }
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.image_one, R.id.image_two})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.image_one:
                /**
                 * 读写SD卡
                 */
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
                break;
            case R.id.image_two:
                requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0002);
                break;
        }
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
                mOneFlag = true;
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .setDir((ArrayList<String>) mListOne)
                        .start(DocCompareActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                mTwoFlag = true;
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .setDir((ArrayList<String>) mListTwo)
                        .start(DocCompareActivity.this, PhotoPicker.REQUEST_CODE);
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
            if (mOneFlag == true && mTwoFlag == false) {
                GlideUtils.loadImageViewLoding(DocCompareActivity.this, photos.get(0), imageOne, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
            } else if (mOneFlag == false && mTwoFlag == true) {
                GlideUtils.loadImageViewLoding(DocCompareActivity.this, photos.get(0), imageTwo, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
            }

        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
