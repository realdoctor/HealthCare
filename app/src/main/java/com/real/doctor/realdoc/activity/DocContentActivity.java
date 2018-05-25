package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.AudioAdapter;
import com.real.doctor.realdoc.adapter.ContentGridAdapter;
import com.real.doctor.realdoc.adapter.ImageCardAdapter;
import com.real.doctor.realdoc.adapter.VideoAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.PlayRecordFragment;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.DragBean;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DocGridView;
import com.real.doctor.realdoc.view.TriangleDrawable;
import com.real.doctor.realdoc.view.popup.BasePopup;
import com.real.doctor.realdoc.view.popup.EasyPopup;
import com.real.doctor.realdoc.view.popup.XGravity;
import com.real.doctor.realdoc.view.popup.YGravity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/25.
 */

public class DocContentActivity extends BaseActivity {

    private static final int REQUEST_CODE_TAKE_MODIFY = 0x110;

    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    SaveDocBean saveDocBean;
    @BindView(R.id.ill)
    TextView ill;
    @BindView(R.id.hospital)
    TextView hospital;
    @BindView(R.id.doctor)
    TextView doctor;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.advice_text)
    TextView adviceText;
    @BindView(R.id.advice)
    TextView advice;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.grid_recycle_view)
    RecyclerView gridRecycleView;
    @BindView(R.id.audio_recycle_view)
    RecyclerView audioRecycleView;
    @BindView(R.id.video_recycle_view)
    RecyclerView videoRecycleView;
    private List<String> mImgPaths;
    private Bitmap[] newImgs;
    private String mFolder;
    private SaveDocManager instance;
    private RecordManager recordInstance;
    private VideoManager videoInstance;
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private List<ImageListBean> imageOriganList;
    private List<ImageBean> imageOriganBean;
    private ImageCardAdapter imageCardAdapter;
    private List<RecordBean> audioList;
    private List<VideoBean> videoList;
    private AudioAdapter audioAdapter;
    private VideoAdapter videoAdapter;
    private List<ImageBean> mAllList;
    private EasyPopup mRightPop;
    private String mAdvice;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_content;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        rightIcon.setVisibility(View.VISIBLE);
        imageOriganList = new ArrayList<>();
        imageOriganBean = new ArrayList<>();
        mAllList = new ArrayList<>();
        audioList = new ArrayList<>();
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
        videoRecycleView.setAdapter(videoAdapter);
        instance = SaveDocManager.getInstance(DocContentActivity.this);
        recordInstance = RecordManager.getInstance(DocContentActivity.this);
        videoInstance = VideoManager.getInstance(DocContentActivity.this);
        imageInstance = ImageManager.getInstance(DocContentActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(DocContentActivity.this);
        saveDocBean = (SaveDocBean) getIntent().getParcelableExtra("SaveDocBean");
        if (EmptyUtils.isNotEmpty(saveDocBean)) {
            String mIll = saveDocBean.getIll().toString().trim();
            String mDoctor = saveDocBean.getDoctor().toString().trim();
            String mHospital = saveDocBean.getHospital().toString().trim();
            String mTime = null;
            String patientDiagId = saveDocBean.getPatientDiagId();
            if (EmptyUtils.isNotEmpty(patientDiagId)) {
                getPatientDiag(patientDiagId);
            }
            if (saveDocBean.getTime() != null) {
                mTime = saveDocBean.getTime().toString().trim();
            }
            if (saveDocBean.getFolder() != null) {
                mFolder = saveDocBean.getFolder().trim();
            }
            if (EmptyUtils.isNotEmpty(mIll)) {
                ill.setText(mIll);
            }
            if (EmptyUtils.isNotEmpty(mHospital)) {
                hospital.setText(mHospital);
            }
            if (EmptyUtils.isNotEmpty(mDoctor)) {
                doctor.setText(mDoctor);
            }
            if (EmptyUtils.isNotEmpty(mTime)) {
                time.setText(DateUtil.timeStamp2Date(mTime, "y年M月d日"));
            }

            String recordId = saveDocBean.getId();
            //获取图片,文字item
            List<ImageListBean> imageList = imageRecycleInstance.queryImageListById(this, recordId);
            int imageListLength = imageList.size();
            for (int i = 0; i < imageListLength; i++) {
                String id = imageList.get(i).getId();
                List<ImageBean> list = imageInstance.queryImageByImageId(this, id);
                imageOriganBean.addAll(list);
                if (list.size() > 0) {
                    imageList.get(i).setmImgUrlList(list);
                    mAllList.addAll(list);
                }
            }
            int length = mAllList.size();
            newImgs = new Bitmap[length];
            mImgPaths = new ArrayList<>();
            for (int j = 0; j < length; j++) {
                String url = mAllList.get(j).getImgUrl();
                mImgPaths.add(url);
                File file = new File(url);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    newImgs[j] = BitmapFactory.decodeStream(fis);
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            imageOriganList.addAll(imageList);
            //添加进recycleview，并将"+"按钮放在recycleview下面
            gridRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            imageCardAdapter = new ImageCardAdapter(this, R.layout.image_card_item, imageOriganList, newImgs, mImgPaths, true);
            //给RecyclerView设置适配器
            gridRecycleView.setAdapter(imageCardAdapter);

            if (EmptyUtils.isNotEmpty(mFolder)) {
                //获取录音的列表
                audioList = recordInstance.queryRecordWithFolder(this, mFolder);
                audioRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
                audioRecycleView.setAdapter(audioAdapter);

                //获取视频的列表
                videoList = videoInstance.queryVideoWithFolder(this, mFolder);
                videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
                videoRecycleView.setAdapter(videoAdapter);
            }
        }
        initAbovePop();
    }

    private void getPatientDiag(String patientDiagId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("patientDiagId", patientDiagId);
        HttpRequestClient.getInstance(DocContentActivity.this).createBaseApi().get("drug"
                , map, new BaseObserver<ResponseBody>(DocContentActivity.this) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(DocContentActivity.this, "获取处方信息失败!");
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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject obj = object.getJSONObject("data");
                                        if (DocUtils.hasValue(obj, "list")) {
                                            List<DragBean> list = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), DragBean.class);
                                            StringBuffer sb = new StringBuffer();
                                            for (int i = 0; i < list.size(); i++) {
                                                sb.append(list.get(i).getDrugName());
                                                sb.append(";");
                                            }
                                            mAdvice = sb.toString().trim();
                                            if (list.size() > 0) {
                                                adviceText.setVisibility(View.VISIBLE);
                                                advice.setVisibility(View.VISIBLE);
                                                advice.setText(mAdvice);
                                            } else {
                                                adviceText.setVisibility(View.GONE);
                                                advice.setVisibility(View.GONE);
                                            }
                                            saveDocBean.setAdvice(mAdvice);
                                            instance.insertSaveDoc(DocContentActivity.this, saveDocBean);
                                        }
                                    }
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

    private void initAbovePop() {

        mRightPop = EasyPopup.create()
                .setContext(this)
                .setContentView(R.layout.right_pop_layout)
                .setAnimationStyle(R.style.RightTopPopAnim)
                .setOnViewListener(new EasyPopup.OnViewListener() {
                    @Override
                    public void initViews(View view) {
                        View arrowView = view.findViewById(R.id.v_arrow);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.parseColor("#ff03b5e5")));
                        }
                    }
                })
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView modify = mRightPop.findViewById(R.id.modify);
        TextView compare = mRightPop.findViewById(R.id.compare);
        modify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocContentActivity.this,
                        SaveRecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("modify", true);
                bundle.putParcelableArrayList("imageOriganList", (ArrayList<?
                        extends Parcelable>) imageOriganList);
                bundle.putParcelableArrayList("audioList", (ArrayList<? extends
                        Parcelable>) audioList);
                bundle.putParcelableArrayList("videoList", (ArrayList<? extends
                        Parcelable>) videoList);
                bundle.putParcelable("saveDocBean", saveDocBean);
                intent.putExtra("data", bundle);
                startActivityForResult(intent, REQUEST_CODE_TAKE_MODIFY);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                mRightPop.dismiss();
            }
        });
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocContentActivity.this,
                        SingleCompareActivity.class);
                intent.putExtra("saveDocBean", saveDocBean);
                startActivity(intent);
                mRightPop.dismiss();
            }
        });
    }

    @Override
    public void initEvent() {
        if (EmptyUtils.isNotEmpty(mFolder)) {
            audioAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    //点击播放录音
                    PlayRecordFragment playRecordFragment =
                            new PlayRecordFragment().newInstance((RecordBean) adapter.getItem(position));
                    FragmentTransaction transaction = DocContentActivity.this.getSupportFragmentManager()
                            .beginTransaction();
                    playRecordFragment.show(transaction, "dialog_play_record");
                }
            });
        }
        if (EmptyUtils.isNotEmpty(mFolder)) {
            videoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    VideoBean videoBean = ((VideoBean) adapter.getItem(position));
                    Intent intent = new Intent(DocContentActivity.this, PlayLocalVideoActivity.class);
                    intent.putExtra("videoBean", videoBean);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
        }

    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_icon})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.right_icon:
                showRightPop(v);
                break;

        }
    }

    private void showRightPop(View view) {
        int offsetX = SizeUtils.dp2px(this, 20) - view.getWidth() / 2;
        int offsetY = (title.getHeight() - view.getHeight()) / 2;
        mRightPop.showAtAnchorView(view, YGravity.BELOW, XGravity.ALIGN_RIGHT, offsetX, offsetY);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_TAKE_MODIFY) {
            List<SaveDocBean> mList = instance.queryRecordId(this, saveDocBean.getId());
            if (mList.size() == 1) {
                saveDocBean = mList.get(0);
                String mIll = saveDocBean.getIll().toString().trim();
                String mDoctor = saveDocBean.getDoctor().toString().trim();
                String mHospital = saveDocBean.getHospital().toString().trim();
                String mTime = null;
                if (saveDocBean.getTime() != null) {
                    mTime = saveDocBean.getTime().toString().trim();
                }
                if (EmptyUtils.isNotEmpty(mIll)) {
                    ill.setText(mIll);
                }
                if (EmptyUtils.isNotEmpty(mHospital)) {
                    hospital.setText(mHospital);
                }
                if (EmptyUtils.isNotEmpty(mDoctor)) {
                    doctor.setText(mDoctor);
                }
                if (EmptyUtils.isNotEmpty(mTime)) {
                    time.setText(DateUtil.timeStamp2Date(mTime, "y年M月d日"));
                }
                imageOriganList.clear();
                for (int i = 0; i < mAllList.size(); i++) {
                    if (newImgs[i] != null && newImgs[i].isRecycled() != false) {
                        newImgs[i].recycle();
                    }
                }
                String recordId = saveDocBean.getId();
                //获取图片,文字item
                List<ImageListBean> imageList = imageRecycleInstance.queryImageListById(this, recordId);
                int imageListLength = imageList.size();
                for (int i = 0; i < imageListLength; i++) {
                    String id = imageList.get(i).getId();
                    List<ImageBean> list = imageInstance.queryImageByImageId(this, id);
                    imageOriganBean.addAll(list);
                    if (list.size() > 0) {
                        imageList.get(i).setmImgUrlList(list);
                        mAllList.addAll(list);
                    }
                }
                int length = mAllList.size();
                newImgs = new Bitmap[length];
                mImgPaths = new ArrayList<>();
                for (int j = 0; j < length; j++) {
                    String url = mAllList.get(j).getImgUrl();
                    mImgPaths.add(url);
                    File file = new File(url);
                    FileInputStream fis = null;
                    try {
                        fis = new FileInputStream(file);
                        newImgs[j] = BitmapFactory.decodeStream(fis);
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                imageOriganList.addAll(imageList);
                //处方信息隐藏
                adviceText.setVisibility(View.GONE);
                advice.setVisibility(View.GONE);
                audioList = new ArrayList<>();
                videoList = new ArrayList<>();
                if (EmptyUtils.isNotEmpty(mFolder)) {
                    //获取录音的列表
                    audioList.addAll(recordInstance.queryRecordWithFolder(this, mFolder));
                    //获取视频的列表
                    videoList.addAll(videoInstance.queryVideoWithFolder(this, mFolder));
                }
                imageCardAdapter.notifyDataSetChanged();
                audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
                audioRecycleView.setAdapter(audioAdapter);
                videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
                videoRecycleView.setAdapter(videoAdapter);
                ToastUtil.showLong(this, "病历修改成功!");
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (saveDocBean.getFolder() != null) {
            for (int i = 0; i < mAllList.size(); i++) {
                if (newImgs[i] != null && newImgs[i].isRecycled() != false) {
                    newImgs[i].recycle();
                }
            }
        }
        System.gc();
    }
}
