package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocCompareAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.ImageManager;
import com.real.doctor.realdoc.greendao.table.ImageRecycleManager;
import com.real.doctor.realdoc.model.CellBean;
import com.real.doctor.realdoc.model.ColBean;
import com.real.doctor.realdoc.model.RowBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.photopicker.PhotoPicker;
import com.real.doctor.realdoc.photopicker.PhotoPreview;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.FileUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.view.excelpanel.ExcelPanel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/27.
 */

public class DocCompareActivity extends BaseActivity {

    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.image_one)
    ImageView imageOne;
    @BindView(R.id.image_two)
    ImageView imageTwo;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.content_container)
    ExcelPanel excelPanel;
    private List<SaveDocBean> mList;
    private List<String> mListOne;
    private List<String> mListTwo;
    private boolean mOneFlag = false;
    private boolean mTwoFlag = false;
    private ImageManager imageInstance;
    private ImageRecycleManager imageRecycleInstance;
    private DocCompareAdapter adapter;
    public static final int ROW_SIZE = 2;
    private List<RowBean> rowTitles;
    private List<ColBean> colTitles;
    private List<List<CellBean>> cells;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        adapter = new DocCompareAdapter(this, blockListener);
        excelPanel.setAdapter(adapter);
        rowTitles = new ArrayList<>();
        colTitles = new ArrayList<>();
        cells = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            cells.add(new ArrayList<CellBean>());
        }
        imageInstance = ImageManager.getInstance(DocCompareActivity.this);
        imageRecycleInstance = ImageRecycleManager.getInstance(DocCompareActivity.this);
        Intent intent = getIntent();
        if (intent != null) {
            mList = getIntent().getParcelableArrayListExtra("mSelectList");
        }
        loadData(false);
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("病历打包上传");
        mListOne = new ArrayList<>();
        mListTwo = new ArrayList<>();

        //获得图片列表
        String idOne = mList.get(0).getId();
        List<String> idOneList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(DocCompareActivity.this), idOne);
        for (int i = 0; i < idOneList.size(); i++) {
            mListOne.addAll(imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(DocCompareActivity.this), idOneList.get(i)));
        }

        //获得图片列表
        String idTwo = mList.get(1).getId();
        List<String> idTwoList = imageRecycleInstance.queryIdList(RealDocApplication.getDaoSession(DocCompareActivity.this), idTwo);
        for (int i = 0; i < idTwoList.size(); i++) {
            mListTwo.addAll(imageInstance.queryImageUrlList(RealDocApplication.getDaoSession(DocCompareActivity.this), idTwoList.get(i)));
        }
        if (mListOne.size() > 0) {
            //图片展示
            if (FileUtils.isFile(mListOne.get(0))) {
                Glide.with(DocCompareActivity.this).load(mListOne.get(0)).crossFade().into(imageOne);
            }
        }
        if (mListTwo.size() > 0) {
            //图片展示
            if (FileUtils.isFile(mListTwo.get(0))) {
                Glide.with(DocCompareActivity.this).load(mListTwo.get(0)).into(imageTwo);
            }
        }
    }

    private void loadData(boolean history) {
        Message message = new Message();
        handler.sendMessageDelayed(message, 1000);
    }

    private View.OnClickListener blockListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CellBean cell = (CellBean) view.getTag();
            if (cell != null) {
                //点击事件
            }
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            List<RowBean> rowTitles = genRowData();
            List<List<CellBean>> cells = genCellData();
            if (colTitles.size() == 0) {
                colTitles.addAll(genColData());
            }
            adapter.setAllData(colTitles, rowTitles, cells);
            adapter.disableHeader();
            adapter.disableFooter();
        }
    };

    //====================================模拟生成数据==========================================
    private List<RowBean> genRowData() {
        List<RowBean> rowTitles = new ArrayList<>();
        RowBean rowBean1 = new RowBean();
        rowBean1.setHeaderContent("疾病名称");
        rowTitles.add(rowBean1);
        RowBean rowBean2 = new RowBean();
        rowBean2.setHeaderContent("就诊医院");
        rowTitles.add(rowBean2);
        RowBean rowBean3 = new RowBean();
        rowBean3.setHeaderContent("就诊医生");
        rowTitles.add(rowBean3);
        RowBean rowBean4 = new RowBean();
        rowBean4.setHeaderContent("就诊时间");
        rowTitles.add(rowBean4);
        return rowTitles;
    }

    private List<List<CellBean>> genCellData() {
        List<List<CellBean>> cells = new ArrayList<>();

        List<CellBean> cellList = new ArrayList<>();
        CellBean cellBean1 = new CellBean();
        String illOne = mList.get(0).getIll();
        cellBean1.setContent(illOne);

        CellBean cellBean2 = new CellBean();
        String hospitalOne = mList.get(0).getHospital();
        cellBean2.setContent(hospitalOne);

        CellBean cellBean3 = new CellBean();
        String doctorOne = mList.get(0).getDoctor();
        cellBean3.setContent(doctorOne);

        CellBean cellBean4 = new CellBean();
        String timeOne = mList.get(0).getTime();
        cellBean4.setContent(DateUtil.timeStamp2Date(timeOne, "yyyy年MM月dd日"));
        cellList.add(cellBean1);
        cellList.add(cellBean2);
        cellList.add(cellBean3);
        cellList.add(cellBean4);
        cells.add(cellList);

        List<CellBean> cellOneList = new ArrayList<>();
        CellBean cellOneBean1 = new CellBean();
        String illTwo = mList.get(1).getIll();
        cellOneBean1.setContent(illTwo);

        CellBean cellOneBean2 = new CellBean();
        String hospitalTwo = mList.get(1).getHospital();
        cellOneBean2.setContent(hospitalTwo);

        CellBean cellOneBean3 = new CellBean();
        String doctorTwo = mList.get(1).getDoctor();
        cellOneBean3.setContent(doctorTwo);

        CellBean cellOneBean4 = new CellBean();
        String timeTwo = mList.get(1).getTime();
        cellOneBean4.setContent(DateUtil.timeStamp2Date(timeTwo, "yyyy年MM月dd日"));
        cellOneList.add(cellOneBean1);
        cellOneList.add(cellOneBean2);
        cellOneList.add(cellOneBean3);
        cellOneList.add(cellOneBean4);
        cells.add(cellOneList);

        return cells;
    }

    private List<ColBean> genColData() {
        List<ColBean> colTitles = new ArrayList<>();
        ColBean colTitle1 = new ColBean();
        colTitle1.setNumber("病历1");
        colTitles.add(colTitle1);
        ColBean colTitle2 = new ColBean();
        colTitle2.setNumber("病历2");
        colTitles.add(colTitle2);
        return colTitles;
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
            List<String> indexs = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                indexs = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_INDEXS);
            }
            if (mOneFlag == true && mTwoFlag == false) {
                GlideUtils.loadImageViewLoding(DocCompareActivity.this, photos.get(0), imageOne, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
//                textOne.setText(firstAdvice[Integer.valueOf(indexs.get(0))]);
            } else if (mOneFlag == false && mTwoFlag == true) {
                GlideUtils.loadImageViewLoding(DocCompareActivity.this, photos.get(0), imageTwo, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
                mOneFlag = false;
                mTwoFlag = false;
//                textTwo.setText(secondAdvice[Integer.valueOf(indexs.get(0))]);
            }

        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
