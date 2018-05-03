package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.GridAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ImageBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SDCardUtils;
import com.real.doctor.realdoc.view.DocGridView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/25.
 */

public class DocContentActivity extends BaseActivity {

    SaveDocBean saveDocBean;
    @BindView(R.id.ill)
    TextView ill;
    @BindView(R.id.hospital)
    TextView hospital;
    @BindView(R.id.doctor)
    TextView doctor;
    @BindView(R.id.doc_grid_view)
    DocGridView docGridView;
    private GridAdapter adapter;
    private List<ImageBean> imageList;
    @BindView(R.id.finish_back)
    ImageView finishBack;

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
        imageList = new ArrayList<>();
        saveDocBean = (SaveDocBean) getIntent().getParcelableExtra("SaveDocBean");
        if (EmptyUtils.isNotEmpty(saveDocBean)) {
            String[] imgs = saveDocBean.getImgs().split(";");
            String mIll = saveDocBean.getIll().toString().trim();
            String mDoctor = saveDocBean.getDoctor().toString().trim();
            String mHospital = saveDocBean.getHospital().toString().trim();
            if (EmptyUtils.isNotEmpty(imgs)) {
                for (int i = 0; i < imgs.length; i++) {
                    ImageBean bean = new ImageBean();
                    bean.setImgUrl(SDCardUtils.getPictureDir() + imgs[i]);
                    imageList.add(bean);
                }
                adapter = new GridAdapter(this, imageList);
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
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
