package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.RecordImgAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.InitCacheFileUtils;
import com.real.doctor.realdoc.util.KeyBoardUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.SelectPopupWindow;
import com.real.doctor.realdoc.widget.OnRecyclerItemClickListener;
import com.real.doctor.realdoc.widget.RecordCallBack;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordImagesActivity extends BaseActivity {

    public static final String FILE_DIR_NAME = "com.real.doctor.realdoc";//应用缓存地址
    public static final String FILE_IMG_NAME = "images";//放置图片缓存
    public static final int IMAGE_SIZE = 9;//可添加图片最大数
    private static final int REQUEST_IMAGE = 0x100;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.delete_text_view)
    TextView deleteTextView;
    @BindView(R.id.record_img_edit)
    EditText recordImgEdit;
    @BindView(R.id.record_recycle_view)
    RecyclerView recordRecycleView;
    @BindView(R.id.record_img_relative)
    RelativeLayout recordImgRelative;
    @BindView(R.id.add_label_btn)
    Button addLabelBtn;
    @BindView(R.id.label_text_relative)
    RelativeLayout labelTextRelative;
    @BindView(R.id.label_text)
    TextView labelText;
    @BindView(R.id.label_icon)
    ImageView labelIcon;
    private ArrayList<String> originImages;//原始图片
    private ArrayList<String> dragImages;//压缩长宽后图片
    private Context mContext;
    private RecordImgAdapter recordImgAdapter;
    private ItemTouchHelper itemTouchHelper;
    private AddLabelBean addLabelBean;
    //拍照
    private static final int REQUEST_CODE_TAKE_PHOTO = 0x110;
    //标签添加
    private static final int REQUEST_CODE_ADD_LABEL = 0x120;
    //底部弹出菜单
    private SelectPopupWindow mPopup;

    private String mCurrentPhotoPath;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_images;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(RecordImagesActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        rightTitle.setText("确定");
        rightTitle.setVisibility(View.VISIBLE);
        originImages = getIntent().getStringArrayListExtra("img");
        mContext = getApplicationContext();
        InitCacheFileUtils.initImgDir(FILE_DIR_NAME, FILE_IMG_NAME);//清除图片缓存
        //添加按钮图片资源
        String plusPath = getString(R.string.glide_plus_icon_string) + "com.real.doctor.realdoc" + File.separator + "mipmap" + File.separator + R.mipmap.add;
        dragImages = new ArrayList<>();
        originImages.add(plusPath);//添加按键，超过9张时在adapter中隐藏
        dragImages.addAll(originImages);
        new Thread(new ImgRunnable(dragImages, originImages, dragImages, imgHandler, false)).start();//开启线程，在新线程中去压缩图片
        initRcv();
        mPopup = new SelectPopupWindow(RecordImagesActivity.this, itemsOnClick);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_title, R.id.add_label_btn})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_title:
                //隐藏键盘
                KeyBoardUtils.closeOrOpenKeybord(this);
                //回调信息(我用广播代替)
                dragImages.remove(dragImages.size() - 1);
                //动态注册广播
                intent = new Intent(SaveRecordActivity.RECORD_IMAGES_TEXT);
                intent.putStringArrayListExtra("imgs", dragImages);
                intent.putExtra("advice", recordImgEdit.getText().toString().trim());
                if (labelTextRelative.getVisibility() == View.VISIBLE) {
                    intent.putExtra("label", addLabelBean);
                } else {
                    intent.putExtra("label", addLabelBean);
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                finish();
                break;
            case R.id.add_label_btn:
                //点击添加标签按钮
                intent = new Intent(this, AddLabelActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_LABEL);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    private void initRcv() {
        recordImgAdapter = new RecordImgAdapter(this, dragImages);
        recordRecycleView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recordRecycleView.setAdapter(recordImgAdapter);
        RecordCallBack myCallBack = new RecordCallBack(recordImgAdapter, dragImages, originImages);
        itemTouchHelper = new ItemTouchHelper(myCallBack);
        itemTouchHelper.attachToRecyclerView(recordRecycleView);//绑定RecyclerView

        //事件监听
        recordRecycleView.addOnItemTouchListener(new OnRecyclerItemClickListener(recordRecycleView) {

            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (originImages.get(vh.getAdapterPosition()).contains(getString(R.string.glide_plus_icon_string))) {//打开相册
                    //显示窗口
                    mPopup.showAtLocation(recordImgRelative, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                    backgroundAlpha(0.5f);
                    mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //改变显示的按钮图片为正常状态
                            backgroundAlpha(1);
                        }
                    });
                } else {
//                    ToastUtil.showLong(RealDocApplication.getContext(), "预览图片");
                }
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                //如果item不是最后一个，则执行拖拽
                if (vh.getLayoutPosition() != dragImages.size() - 1) {
                    itemTouchHelper.startDrag(vh);
                }
            }
        });

        myCallBack.setDragListener(new RecordCallBack.DragListener() {
            @Override
            public void deleteState(boolean delete) {
                if (delete) {
                    deleteTextView.setBackgroundResource(R.color.holo_red_dark);
                    deleteTextView.setText(getResources().getString(R.string.post_delete_tv_s));
                } else {
                    deleteTextView.setText(getResources().getString(R.string.post_delete_tv_d));
                    deleteTextView.setBackgroundResource(R.color.holo_red_light);
                }
            }

            @Override
            public void dragState(boolean start) {
                if (start) {
                    deleteTextView.setVisibility(View.VISIBLE);
                } else {
                    deleteTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void clearView() {
            }
        });
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
            //压缩图片
            new Thread(new ImgRunnable((ArrayList<String>) photos,
                    originImages, dragImages, imgHandler, true)).start();
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_PHOTO) {
            photos.add(mCurrentPhotoPath);
            //压缩图片
            new Thread(new ImgRunnable((ArrayList<String>) photos,
                    originImages, dragImages, imgHandler, true)).start();
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_LABEL) {
            //添加标签
            labelTextRelative.setVisibility(View.VISIBLE);
            addLabelBean = data.getParcelableExtra("addLabelBean");
            labelText.setText(addLabelBean.getName());
            String icon = addLabelBean.getIcon();
            if (EmptyUtils.isNotEmpty(icon)) {
                Glide.with(mContext).load(icon).crossFade().into((ImageView) labelIcon);
            }
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

    public static void startRecordActivity(Context context, ArrayList<String> images) {
        Intent intent = new Intent(context, RecordImagesActivity.class);
        intent.putStringArrayListExtra("img", images);
        context.startActivity(intent);
    }

    /**
     * 另起线程压缩图片
     */
    static class ImgRunnable implements Runnable {

        ArrayList<String> images;
        ArrayList<String> originImages;
        ArrayList<String> dragImages;
        Handler handler;
        boolean add;//是否为添加图片

        public ImgRunnable(ArrayList<String> images, ArrayList<String> originImages, ArrayList<String> dragImages, Handler handler, boolean add) {
            this.images = images;
            this.originImages = originImages;
            this.dragImages = dragImages;
            this.handler = handler;
            this.add = add;
        }

        @Override
        public void run() {
            String filePath;
            Bitmap newBitmap;
            int addIndex = originImages.size() - 1;
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).contains(RealDocApplication.getContext().getString(R.string.glide_plus_icon_string))) {//说明是添加图片按钮
                    continue;
                }
                //压缩
                newBitmap = ImageUtils.getSmallBitmap(images.get(i),
                        SizeUtils.dp2px(RealDocApplication.getContext(), 100),
                        SizeUtils.dp2px(RealDocApplication.getContext(), 100));
                //文件地址
                filePath = SDCardUtils.getSDCardPath() + FILE_DIR_NAME + "/"
                        + FILE_IMG_NAME + "/" + String.format("img_%d.jpg", System.currentTimeMillis());
                //保存图片
                ImageUtils.save(newBitmap, filePath, Bitmap.CompressFormat.JPEG, true);
                //设置值
                if (!add) {
                    images.set(i, filePath);
                } else {//添加图片，要更新
                    dragImages.add(addIndex, filePath);
                    originImages.add(addIndex++, filePath);
                }
            }
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
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

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        // 类型
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    private ImgHandler imgHandler = new ImgHandler(this);

    private class ImgHandler extends Handler {
        private WeakReference<Activity> reference;

        public ImgHandler(Activity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecordImagesActivity activity = (RecordImagesActivity) reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        activity.recordImgAdapter.notifyDataSetChanged();
                        break;
                }
            }
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
                PhotoPicker.builder()
                        .setPhotoCount(IMAGE_SIZE - originImages.size())
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .start(RecordImagesActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                takePhotoCompress();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imgHandler.removeCallbacksAndMessages(null);
    }
}
