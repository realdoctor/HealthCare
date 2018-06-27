package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.LabelBean;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.view.LabelsView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IllLabelActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.ill_labels)
    LabelsView illLabels;
    //疾病标签
    ArrayList<LabelBean> labelList = new ArrayList<>();
    private SaveDocManager instance = null;
    private List<String> diseasesList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ill_label;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(IllLabelActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        instance = SaveDocManager.getInstance(IllLabelActivity.this);
        //疾病列表
        diseasesList = instance.queryDiseaseList(RealDocApplication.getDaoSession(IllLabelActivity.this));
        diseasesList.remove(0);
        diseasesList.remove(0);
        for (int i = 1; i <= diseasesList.size(); i++) {
            labelList.add(new LabelBean(diseasesList.get(i - 1), i));
        }
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
                intent.putExtra("disease", illLabel);
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
