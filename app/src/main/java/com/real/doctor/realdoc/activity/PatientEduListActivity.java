package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.AudioAdapter;
import com.real.doctor.realdoc.adapter.ImageCardAdapter;
import com.real.doctor.realdoc.adapter.VideoAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.AlreadyRevisitFragment;
import com.real.doctor.realdoc.fragment.InfoFragment;
import com.real.doctor.realdoc.fragment.RevisitingFragment;
import com.real.doctor.realdoc.fragment.VideoFragment;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.view.SelectPopupWindow;
import com.real.doctor.realdoc.view.SelectPopupWindowUpload;
import com.real.doctor.realdoc.widget.TabViewPagerAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientEduListActivity extends BaseActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.right_title)
    TextView right_title;
    private TabViewPagerAdapter viewPagerAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"图文", "视频"};
    private List<Fragment> fragments = new ArrayList<>();
    //底部弹出菜单
    private SelectPopupWindowUpload mPopup;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    private String mCurrentPhotoPath;
    private String folder;
    public String id="";
    @Override
    public int getLayoutId() {
        return R.layout.activity_patient_edu_show;
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(PatientEduListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        id=getIntent().getStringExtra("id");
        if(id.length()!=0) {
            right_title.setVisibility(View.GONE);
        }else{
            right_title.setVisibility(View.VISIBLE);
            right_title.setText("上传");
        }
        pageTitle.setText("咨询");
        mPopup = new SelectPopupWindowUpload(PatientEduListActivity.this, itemsOnClick);
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
            }  else if (i == R.id.add_video) {
                if (DocUtils.isFastClick()) {
                    Intent intent = new Intent(PatientEduListActivity.this, VideoOneActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("folder", folder);
                    bundle.putInt("key",1);
                    intent.putExtras(bundle);
                    //startActivityForResult(intent, 112);
                    startActivity(intent);
                }
            }
        }
    };

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 类型
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void initData() {
        String folderTime = DateUtil.timeStamp();
        folder = String.valueOf(folderTime);
        //设置TabLayout标签的显示方式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        //设置TabLayout点击事件
        tabLayout.setOnTabSelectedListener(this);
        fragments.add(InfoFragment.newInstance(id));
        fragments.add(VideoFragment.newInstance(id));
        viewPagerAdapter = new TabViewPagerAdapter(getSupportFragmentManager(), titles, fragments);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.layout_divider_vertical));
        linearLayout.setDividerPadding(SizeUtils.dp2px(this, 15));
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back,R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_title:
                mPopup.showAtLocation(right_title, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                backgroundAlpha(0.5f);
                mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //改变显示的按钮图片为正常状态
                        backgroundAlpha(1);
                    }
                });
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

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
                        .setPhotoCount(PhotoPicker.DEFAULT_MAX_COUNT)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .start(PatientEduListActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<String> photos = new ArrayList<>();
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            PublicImagesActivity.startRecordActivity(PatientEduListActivity.this,
                    (ArrayList<String>) photos);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            photos.add(mCurrentPhotoPath);
            PublicImagesActivity.startRecordActivity(PatientEduListActivity.this,
                    (ArrayList<String>) photos);
        } else if (resultCode == RESULT_OK && requestCode == 112) {
//            // 从数据库中取出视频列表,并展示
//            videoList = videoInstance.queryVideoWithFolder(SaveRecordActivity.this, folder);
//            if (EmptyUtils.isNotEmpty(videoList) && videoList.size() > 0) {
//                videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//                //notifyDataSetChanged()
//                videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
//                videoRecycleView.setAdapter(videoAdapter);
//                initEvent();
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
}
