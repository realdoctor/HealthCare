package com.real.doctor.realdoc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.DaoSession;
import com.real.doctor.realdoc.greendao.ImageBeanDao;
import com.real.doctor.realdoc.greendao.ImageListBeanDao;
import com.real.doctor.realdoc.greendao.RecordBeanDao;
import com.real.doctor.realdoc.greendao.SaveDocBeanDao;
import com.real.doctor.realdoc.greendao.VideoBeanDao;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.ImageUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.CleanMessageUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.account_set)
    LinearLayout accountSet;
    @BindView(R.id.address_set)
    LinearLayout addressSet;
    @BindView(R.id.clean_cache)
    LinearLayout cleanCache;
    @BindView(R.id.user_fade)
    LinearLayout userFade;
    @BindView(R.id.about_us)
    LinearLayout aboutUs;
    @BindView(R.id.login_out)
    Button loginOut;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.cache)
    TextView cache;

    private CommonDialog dialog;
    private String avator;
    private String mobile;
    private String token;
    private String path;
    private SaveDocManager instance;
    //从数据库中获取数据
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SettingActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("设置");
        localBroadcast();
    }

    @Override
    public void initData() {
        try {
            String size = CleanMessageUtil.getTotalCacheSize(SettingActivity.this);
            cache.setText("清除缓存" + CleanMessageUtil.getFormatSize(Double.valueOf(size)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = SaveDocManager.getInstance(SettingActivity.this);
        imageInstance = ImageManager.getInstance(SettingActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(SettingActivity.this);
        recordInstance = RecordManager.getInstance(SettingActivity.this);
        videoInstance = VideoManager.getInstance(SettingActivity.this);
        mobile = (String) SPUtils.get(SettingActivity.this, Constants.MOBILE, "");
        token = (String) SPUtils.get(SettingActivity.this, Constants.TOKEN, "");
        if (EmptyUtils.isNotEmpty(token)) {
            loginOut.setVisibility(View.VISIBLE);
        } else {
            loginOut.setVisibility(View.GONE);
        }
        //获得头像
        StringBuffer sb = new StringBuffer();
        sb.append(SDCardUtils.getGlobalDir());
        sb.append("avater");
        sb.append(mobile);
        sb.append(".png");
        String avaterPath = sb.toString();
        if (FileUtils.isFileExists(avaterPath)) {
            Bitmap bitmap = ImageUtils.getSmallBitmap(avaterPath,
                    SizeUtils.dp2px(SettingActivity.this, 50),
                    SizeUtils.dp2px(SettingActivity.this, 50));
            icon.setImageBitmap(bitmap);
        }
        //获取头像
        Intent intent = getIntent();
        avator = intent.getExtras().getString("imgUrl");
        if (EmptyUtils.isNotEmpty(avator)) {
            GlideUtils.loadImageViewDiskCache(RealDocApplication.getContext(), avator, icon);
        }
    }

    @Override
    public void initEvent() {

    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(SettingActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountActivity.CHANGE_AVATOR);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String imageUrl = (String) intent.getExtras().get("avator");
                if (EmptyUtils.isNotEmpty(imageUrl)) {
                    Bitmap bitmap = ImageUtils.getSmallBitmap(imageUrl, SizeUtils.dp2px(SettingActivity.this, 50), SizeUtils.dp2px(SettingActivity.this, 50));
                    icon.setImageBitmap(bitmap);
                }
//                GlideUtils.loadImageViewLoding(RealDocApplication.getContext(), avator, icon, R.mipmap.ease_default_avatar, R.mipmap.ease_default_avatar);
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    @OnClick({R.id.finish_back, R.id.login_out, R.id.account_set, R.id.address_set, R.id.clean_cache, R.id.user_fade, R.id.about_us})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.login_out:
                //弹出框界面
                dialog = new CommonDialog(this).builder()
                        .setCancelable(false)
                        .setContent("确定要退出吗？")
                        .setCanceledOnTouchOutside(true)
                        .setCancelClickBtn(new CommonDialog.CancelListener() {

                            @Override
                            public void onCancelListener() {
                                dialog.dismiss();
                            }
                        }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                            @Override
                            public void onConfrimClick() {
                                loginOut();
                            }
                        }).show();
                break;
            case R.id.account_set:
                //账号设置
                intent = new Intent(SettingActivity.this, AccountActivity.class);
                intent.putExtra("avator", avator);
                startActivity(intent);
                break;
            case R.id.address_set:
                //地址设置
                intent = new Intent(SettingActivity.this, AddressListActivity.class);
                startActivity(intent);
                break;
            case R.id.clean_cache:
                //清除缓存
                //弹出框界面
                dialog = new CommonDialog(this).builder()
                        .setCancelable(false)
                        .setContent("确定清除缓存吗？")
                        .setCanceledOnTouchOutside(true)
                        .setCancelClickBtn(new CommonDialog.CancelListener() {

                            @Override
                            public void onCancelListener() {
                                dialog.dismiss();
                            }
                        }).setConfirmClickBtn(new CommonDialog.ConfirmListener() {

                            @Override
                            public void onConfrimClick() {
                                CleanMessageUtil.clearAllCache(SettingActivity.this);
                            }
                        }).show();
                break;
            case R.id.user_fade:
                //用户反馈
                intent = new Intent(SettingActivity.this, UserFadeActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                //关于我们
                break;
        }
    }

    private void loginOut() {
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), "");
        HttpRequestClient.getInstance(SettingActivity.this).createBaseApi().json(" user/logout/"
                , body, new BaseObserver<ResponseBody>(SettingActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(SettingActivity.this, "用户退出失败!");
                        Log.d(TAG, e.getMessage());
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
                                    ToastUtil.showLong(RealDocApplication.getContext(), "用户退出成功!");
                                    //环信登出
                                    loginOutHuanXin();
                                    setExternalData(mobile);
                                    //删除掉里面添加的,删除掉有folder的
                                    deleteInsideData();
                                    //插入外面已经添加的
                                    getExternalData("external");
                                    //隐藏退出登录按钮
                                    loginOut.setVisibility(View.GONE);
                                    //跳转到首页
                                    Intent intent = new Intent(SettingActivity.this, RealDocActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    SPUtils.clear(SettingActivity.this);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    ToastUtil.showLong(SettingActivity.this, "用户退出失败!");
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

    private void deleteInsideData() {
        //将数据库导出到文件夹中,并且覆盖文件夹中的数据库
        List<SaveDocBean> list = instance.querySaveDocListByFolder(SettingActivity.this);
        SaveDocBeanDao saveDocDao = RealDocApplication.getDaoSession(SettingActivity.this).getSaveDocBeanDao();
        saveDocDao.deleteInTx(list);
        ImageListBeanDao imageListBeanDao = RealDocApplication.getDaoSession(SettingActivity.this).getImageListBeanDao();
        imageListBeanDao.deleteAll();
        ImageBeanDao imageBeanDao = RealDocApplication.getDaoSession(SettingActivity.this).getImageBeanDao();
        imageBeanDao.deleteAll();
        VideoBeanDao videoBeanDao = RealDocApplication.getDaoSession(SettingActivity.this).getVideoBeanDao();
        videoBeanDao.deleteAll();
        RecordBeanDao recordBeanDao = RealDocApplication.getDaoSession(SettingActivity.this).getRecordBeanDao();
        recordBeanDao.deleteAll();
    }

    private void setExternalData(String mobile) {
        //将数据库导出到文件夹中,并且覆盖文件夹中的数据库
        List<SaveDocBean> list = instance.querySaveDocList(SettingActivity.this);
        instance.insertGlobeSaveDoc(SettingActivity.this, list, mobile, SDCardUtils.getGlobalDir());
        List<ImageBean> beanList = imageInstance.queryImageList(SettingActivity.this);
        imageInstance.insertGlobeImageList(SettingActivity.this, beanList, mobile, SDCardUtils.getGlobalDir());
        List<ImageListBean> imageList = imageRecycleInstance.queryImageListList(SettingActivity.this);
        imageRecycleInstance.insertGlobelImageListList(SettingActivity.this, imageList, mobile, SDCardUtils.getGlobalDir());
        List<RecordBean> recordList = recordInstance.queryRecordList(SettingActivity.this);
        recordInstance.insertGlobeRecordList(SettingActivity.this, recordList, mobile, SDCardUtils.getGlobalDir());
        List<VideoBean> videoList = videoInstance.queryVideoList(SettingActivity.this);
        videoInstance.insertGlobeVideoList(SettingActivity.this, videoList, mobile, SDCardUtils.getGlobalDir());
    }

    private void getExternalData(String mobile) {
        //是否该账号有外部数据库，如果有则将外部数据库导入现在的数据库中,否则就不做处理
        StringBuffer sb = new StringBuffer();
        sb.append(SDCardUtils.getGlobalDir());
        sb.append("datebases");
        sb.append(File.separator);
        sb.append(mobile + ".db");
        path = sb.toString();
        if (FileUtils.isFileExists(path)) {
            //将外部数据库导入内部,并删除内部数据库
            List<SaveDocBean> list = instance.queryGlobeSaveDocList(SettingActivity.this, mobile, SDCardUtils.getGlobalDir());
            instance.insertSaveDoc(SettingActivity.this, list);
            List<ImageBean> beanList = imageInstance.queryGlobeImage(SettingActivity.this, mobile, SDCardUtils.getGlobalDir());
            imageInstance.insertImageList(SettingActivity.this, beanList);
            List<ImageListBean> imageList = imageRecycleInstance.queryGlobeImageList(SettingActivity.this, mobile, SDCardUtils.getGlobalDir());
            imageRecycleInstance.insertImageListList(SettingActivity.this, imageList);
            List<RecordBean> recordList = recordInstance.queryGlobeRecord(SettingActivity.this, mobile, SDCardUtils.getGlobalDir());
            recordInstance.insertRecordList(SettingActivity.this, recordList);
            List<VideoBean> videoList = videoInstance.queryGlobeVideo(SettingActivity.this, mobile, SDCardUtils.getGlobalDir());
            videoInstance.insertVideoList(SettingActivity.this, videoList);
            //删除数据库
            FileUtils.deleteDir(path);
        }
    }

    private void loginOutHuanXin() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d(TAG, "loginOut: onSuccess");
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                Log.d(TAG, "loginOut: onError");
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}