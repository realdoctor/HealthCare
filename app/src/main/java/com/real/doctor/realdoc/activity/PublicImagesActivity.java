package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.RecordImgAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.model.OrderModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileProvider7;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.InitCacheFileUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.SelectPopupWindow;
import com.real.doctor.realdoc.widget.OnRecyclerItemClickListener;
import com.real.doctor.realdoc.widget.RecordCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
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
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class PublicImagesActivity extends BaseActivity {

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
    @BindView(R.id.et_price)
    EditText et_price;
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
        return R.layout.activity_public_images;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(PublicImagesActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        rightTitle.setText("上传");
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
        mPopup = new SelectPopupWindow(PublicImagesActivity.this, itemsOnClick);
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
               String inputStr= recordImgEdit.getText().toString();
               if(inputStr==null||inputStr.length()==0){
                   Toast.makeText(PublicImagesActivity.this,"请输入描述信息",Toast.LENGTH_SHORT).show();
                   return;
               }

                uploadIcon(originImages,inputStr);
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

    private void uploadIcon(ArrayList<String> paths,String inputStr) {
        if (NetworkUtil.isNetworkAvailable(PublicImagesActivity.this)) {
            Map<String, RequestBody> maps = new HashMap<>();
            maps.put("content", DocUtils.toRequestBodyOfText(inputStr));
            for(String str:paths) {
                File file = new File(String.valueOf(str));
                if (file.exists()) {
                    RequestBody requestBody = DocUtils.toRequestBodyOfImage(file);
                    maps.put("attach\"; filename=\"" + file.getName() + "", requestBody);//head_img图片key
                }
            }

            HttpRequestClient.getInstance(PublicImagesActivity.this).createBaseApi().uploads("upload/uploadFiles/", maps, new BaseObserver<ResponseBody>(PublicImagesActivity.this) {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                protected void onHandleSuccess(ResponseBody responseBody) {
                    //上传文件成功
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
                                ToastUtil.showLong(RealDocApplication.getContext(), "图片上传成功!");
                                PublicImagesActivity.this.finish();
                            } else {
                                ToastUtil.showLong(RealDocApplication.getContext(), "图片上传失败!");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    ToastUtil.showLong(PublicImagesActivity.this, e.getMessage());
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onComplete() {

                }
            });
        }
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
        Intent intent = new Intent(context, PublicImagesActivity.class);
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
            final String[] filePath = new String[1];
            final Bitmap[] newBitmap = new Bitmap[1];
            final int[] addIndex = {originImages.size() - 1};
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).contains(RealDocApplication.getContext().getString(R.string.glide_plus_icon_string))) {//说明是添加图片按钮
                    continue;
                }
                //压缩
//                newBitmap = ImageUtils.getSmallBitmap(images.get(i),
//                        SizeUtils.dp2px(RealDocApplication.getContext(), 100),
//                        SizeUtils.dp2px(RealDocApplication.getContext(), 100));
                final int finalI = i;
                Luban.with(RealDocApplication.getContext())
                        .load(images.get(i))
                        .ignoreBy(100)
                        .filter(new CompressionPredicate() {
                            @Override
                            public boolean apply(String path) {
                                return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                            }
                        })
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                // TODO 压缩成功后调用，返回压缩后的图片文件
                                newBitmap[0] = BitmapFactory.decodeFile(file.getAbsolutePath());//filePath
                                //文件地址
                                filePath[0] = SDCardUtils.getSDCardPath() + FILE_DIR_NAME + "/"
                                        + FILE_IMG_NAME + "/" + String.format("img_%d.jpg", System.currentTimeMillis());
                                //保存图片
                                ImageUtils.save(newBitmap[0], filePath[0], Bitmap.CompressFormat.JPEG, true);
                                //设置值
                                if (!add) {
                                    images.set(finalI, filePath[0]);
                                } else {//添加图片，要更新
                                    dragImages.add(addIndex[0], filePath[0]);
                                    originImages.add(addIndex[0]++, filePath[0]);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                            }
                        }).launch();

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
            PublicImagesActivity activity = (PublicImagesActivity) reference.get();
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
                        .start(PublicImagesActivity.this, PhotoPicker.REQUEST_CODE);
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
