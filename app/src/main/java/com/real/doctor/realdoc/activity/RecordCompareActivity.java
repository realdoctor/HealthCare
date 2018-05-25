package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.RecordComparedAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.model.RecordTableBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.GlideUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/27.
 */

public class RecordCompareActivity extends BaseActivity {

    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.image_one)
    ImageView imageOne;
    @BindView(R.id.image_two)
    ImageView imageTwo;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    private List<SaveDocBean> mList;
    private List<String> mListOne;
    private List<String> mListTwo;
    private List<RecordTableBean> tableBean;
    private boolean mOneFlag = false;
    private boolean mTwoFlag = false;
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private RecordComparedAdapter recordComparedAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        imageInstance = ImageManager.getInstance(RecordCompareActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(RecordCompareActivity.this);
        Intent intent = getIntent();
        if (intent != null) {
            mList = getIntent().getParcelableArrayListExtra("mSaveList");
        }
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("病历打包上传");
        mListOne = new ArrayList<>();
        mListTwo = new ArrayList<>();

        //获得图片列表
        String idOne = mList.get(0).getId();
        List<String> idOneList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(RecordCompareActivity.this), idOne);
        for (int i = 0; i < idOneList.size(); i++) {
            mListOne.addAll(imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(RecordCompareActivity.this), idOneList.get(i)));
        }

        //获得图片列表
        String idTwo = mList.get(1).getId();
        List<String> idTwoList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(RecordCompareActivity.this), idTwo);
        for (int i = 0; i < idTwoList.size(); i++) {
            mListTwo.addAll(imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(RecordCompareActivity.this), idTwoList.get(i)));
        }
        if (mListOne.size() > 0) {
            //图片展示
            if (FileUtils.isFile(mListOne.get(0))) {
                Glide.with(RecordCompareActivity.this).load(mListOne.get(0)).crossFade().into(imageOne);
            }
        } else {
            imageOne.setVisibility(View.GONE);
        }
        if (mListTwo.size() > 0) {
            //图片展示
            if (FileUtils.isFile(mListTwo.get(0))) {
                Glide.with(RecordCompareActivity.this).load(mListTwo.get(0)).into(imageTwo);
            }
        } else {
            imageTwo.setVisibility(View.GONE);
        }
        initTable();
    }

    private void initTable() {
        //模拟的假数据（实际开发中当然是从网络获取数据）
        tableBean = new ArrayList<>();
        RecordTableBean bean0 = new RecordTableBean();
        bean0.setItemType(1);
        tableBean.add(bean0);
        RecordTableBean bean = new RecordTableBean();
        bean.setContent("疾病");
        bean.setItemType(2);
        bean.setFristContent(mList.get(0).getIll());
        bean.setSecondContent(mList.get(1).getIll());
        tableBean.add(bean);
        RecordTableBean bean1 = new RecordTableBean();
        bean1.setContent("就诊医院");
        bean1.setItemType(2);
        bean1.setFristContent(mList.get(0).getHospital());
        bean1.setSecondContent(mList.get(1).getHospital());
        tableBean.add(bean1);
        RecordTableBean bean2 = new RecordTableBean();
        bean2.setContent("就诊医生");
        bean2.setItemType(2);
        bean2.setFristContent(mList.get(0).getDoctor());
        bean2.setSecondContent(mList.get(1).getDoctor());
        tableBean.add(bean2);
        RecordTableBean bean3 = new RecordTableBean();
        bean3.setContent("就诊时间");
        bean3.setItemType(2);
        bean3.setFristContent(DateUtil.timeStamp2Date(mList.get(0).getTime(), "y年M月d日"));
        bean3.setSecondContent(DateUtil.timeStamp2Date(mList.get(1).getTime(), "y年M月d日"));
        tableBean.add(bean3);
        String advice1 = mList.get(0).getAdvice();
        String advice2 = mList.get(1).getAdvice();
        if (EmptyUtils.isNotEmpty(advice1) || EmptyUtils.isNotEmpty(advice2)) {
            RecordTableBean bean4 = new RecordTableBean();
            bean4.setContent("处方");
            bean4.setItemType(2);
            bean4.setFristContent(advice1);
            bean4.setSecondContent(advice2);
            tableBean.add(bean4);
        }
        //创建布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //添加Android自带的分割线
        recycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleView.setLayoutManager(layoutManager);
        //创建适配器
        recordComparedAdapter = new RecordComparedAdapter(tableBean);
        //给RecyclerView设置适配器
        recycleView.setAdapter(recordComparedAdapter);

    }


    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.image_one, R.id.image_two, R.id.right_title})
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
            case R.id.right_title:
                actionStart(this, CheckDocActivity.class);
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
                        .start(RecordCompareActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case 0x0002:
                mTwoFlag = true;
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setShowGif(false)
                        .setPreviewEnabled(true)//是否可以预览
                        .setDir((ArrayList<String>) mListTwo)
                        .start(RecordCompareActivity.this, PhotoPicker.REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            List<String> indexs = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                indexs = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_INDEXS);
            }
            if (mOneFlag == true && mTwoFlag == false) {
                GlideUtils.loadImageViewLoding(RecordCompareActivity.this, photos.get(0), imageOne, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
            } else if (mOneFlag == false && mTwoFlag == true) {
                GlideUtils.loadImageViewLoding(RecordCompareActivity.this, photos.get(0), imageTwo, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
            }

        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
