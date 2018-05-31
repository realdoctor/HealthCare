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

public class HospitalLabelActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.hospital_labels)
    LabelsView hospitalLabels;
    //医院标签
    ArrayList<LabelBean> hospitalList = new ArrayList<>();
    private SaveDocManager instance = null;
    private List<String> hospitalsList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_hospital_label;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(HospitalLabelActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        instance = SaveDocManager.getInstance(HospitalLabelActivity.this);
        hospitalsList = instance.queryHospitalList(RealDocApplication.getDaoSession(HospitalLabelActivity.this));
        hospitalsList.remove(0);
        hospitalsList.remove(0);
        for (int i = 1; i <= hospitalsList.size(); i++) {
            hospitalList.add(new LabelBean(hospitalsList.get(i - 1), i));
        }
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
                intent.putExtra("hospital", hospitalLabel);
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
