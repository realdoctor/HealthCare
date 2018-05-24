package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.AudioAdapter;
import com.real.doctor.realdoc.adapter.ContentGridAdapter;
import com.real.doctor.realdoc.adapter.ImageCardAdapter;
import com.real.doctor.realdoc.adapter.VideoAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.PlayRecordFragment;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.greendao.table.RecordManager;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.ImageListBean;
import com.real.doctor.realdoc.model.RecordBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.view.DocGridView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/25.
 */

public class DocContentActivity extends BaseActivity {

    @BindView(R.id.right_title)
    TextView rightTitle;
    SaveDocBean saveDocBean;
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
    @BindView(R.id.grid_recycle_view)
    RecyclerView gridRecycleView;
    @BindView(R.id.audio_recycle_view)
    RecyclerView audioRecycleView;
    @BindView(R.id.video_recycle_view)
    RecyclerView videoRecycleView;
    private String[] imgs;
    private String[] advice;
    private Bitmap[] newImgs;
    private String mFolder;
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
        imageOriganList = new ArrayList<>();
        imageOriganBean = new ArrayList<>();
        audioList = new ArrayList<>();
        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
        videoRecycleView.setAdapter(videoAdapter);
        recordInstance = RecordManager.getInstance(DocContentActivity.this);
        videoInstance = VideoManager.getInstance(DocContentActivity.this);
        imageInstance = ImageManager.getInstance(DocContentActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(DocContentActivity.this);
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("病历对照");
        saveDocBean = (SaveDocBean) getIntent().getParcelableExtra("SaveDocBean");
        if (EmptyUtils.isNotEmpty(saveDocBean)) {
            String mIll = saveDocBean.getIll().toString().trim();
            String mDoctor = saveDocBean.getDoctor().toString().trim();
            String mHospital = saveDocBean.getHospital().toString().trim();
            String mTime = null;
            if (saveDocBean.getTime() != null) {
                mTime = saveDocBean.getTime().toString().trim();
            }
            if (saveDocBean.getFolder() != null) {
                mFolder = saveDocBean.getFolder().trim();

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
            } else {
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
                }
            }
            imageOriganList.addAll(imageList);
            //添加进recycleview，并将"+"按钮放在recycleview下面
            gridRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            imageCardAdapter = new ImageCardAdapter(this, R.layout.image_card_item, imageOriganList);
            //给RecyclerView设置适配器
            gridRecycleView.setAdapter(imageCardAdapter);

            if (EmptyUtils.isNotEmpty(mFolder)) {
                //获取录音的列表
                List<RecordBean> audioList = recordInstance.queryRecordWithFolder(this, mFolder);
                audioRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                audioAdapter = new AudioAdapter(R.layout.audio_item, audioList);
                audioRecycleView.setAdapter(audioAdapter);

                //获取视频的列表
                List<VideoBean> videoList = videoInstance.queryVideoWithFolder(this, mFolder);
                videoRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                videoAdapter = new VideoAdapter(R.layout.video_item, videoList);
                videoRecycleView.setAdapter(videoAdapter);
            }
        }
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
    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.right_title:
                actionStart(this, CheckCompareActivity.class);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    protected void onDestroy() {
        super.onDestroy();
//        if (saveDocBean.getFolder() != null) {
//            for (int i = 0; i < imgs.length; i++) {
//                if (newImgs[i] != null && newImgs[i].isRecycled() != false) {
//                    newImgs[i].recycle();
//                }
//            }
//        }
        System.gc();
    }
}
