package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.LabelBean;
import com.real.doctor.realdoc.view.LabelsView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IllLabelActivity extends BaseActivity {

    @BindView(R.id.ill_labels)
    LabelsView illLabels;
    //疾病标签
    ArrayList<LabelBean> labelList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_ill_label;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        labelList.add(new LabelBean("老年痴呆", 1));
        labelList.add(new LabelBean("脑结核瘤", 2));
        labelList.add(new LabelBean("颅咽管瘤", 3));
        labelList.add(new LabelBean("妇科病", 4));
        illLabels.setLabels(labelList, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
    }

    @Override
    public void initEvent() {
        illLabels.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                LabelBean illObject = (LabelBean) data;
                String illLabel = illObject.getName();
                Intent intent = new Intent();
                intent.putExtra("disease",illLabel);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
