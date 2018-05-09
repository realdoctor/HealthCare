package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.LabelBean;
import com.real.doctor.realdoc.view.LabelsView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HospitalLabelActivity extends BaseActivity {

    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.hospital_labels)
    LabelsView hospitalLabels;
    //医院标签
    ArrayList<LabelBean> hospitalList = new ArrayList<>();


    @Override
    public int getLayoutId() {
        return R.layout.activity_hospital_label;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        hospitalList.add(new LabelBean("长江肛肠科医院", 1));
        hospitalList.add(new LabelBean("第七人民医院", 2));
        hospitalList.add(new LabelBean("颅咽管瘤医院", 3));
        hospitalList.add(new LabelBean("中医药医院", 4));
        hospitalLabels.setLabels(hospitalList, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
    }

    @Override
    public void initEvent() {
        hospitalLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                LabelBean hospitalObject = (LabelBean) data;
                String hospitalLabel = hospitalObject.getName();
                Intent intent = new Intent();
                intent.putExtra("hospital",hospitalLabel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    @OnClick(R.id.finish_back)
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }
    @Override
    public void doBusiness(Context mContext) {

    }
}
