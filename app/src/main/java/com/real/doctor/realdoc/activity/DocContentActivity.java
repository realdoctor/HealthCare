package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ContentGridAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.SaveDocBean;
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
    @BindView(R.id.doc_grid_view)
    DocGridView docGridView;
    private ContentGridAdapter adapter;
    private List<ImageBean> imageList;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    private String[] imgs;
    private String[] advice;
    private Bitmap[] newImgs;
    private String mFolder;

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
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("比例对照");
        imageList = new ArrayList<>();
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
                imgs = saveDocBean.getImgs().split(";");
                advice = saveDocBean.getAdvice().split(";");
                newImgs = new Bitmap[imgs.length];
                if (EmptyUtils.isNotEmpty(mFolder)) {
                    for (int i = 0; i < imgs.length; i++) {
                        ImageBean bean = new ImageBean();
                        String path = SDCardUtils.getPictureDir() + mFolder + File.separator + imgs[i];
                        bean.setImgUrl(path);
                        if (EmptyUtils.isNotEmpty(advice)) {
                            bean.setAdvice(advice[i]);
                        }
                        File file = new File(path);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(file);
                            newImgs[i] = BitmapFactory.decodeStream(fis);
                            fis.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        imageList.add(bean);
                    }
                    adapter = new ContentGridAdapter(this, imageList, newImgs);
                    docGridView.setAdapter(adapter);
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
        }
    }

    @Override
    public void initEvent() {

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
        if (saveDocBean.getFolder() != null) {
            for (int i = 0; i < imgs.length; i++) {
                if (newImgs[i] != null && newImgs[i].isRecycled() != false) {
                    newImgs[i].recycle();
                }
            }
        }
        System.gc();
    }
}
