package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CaseControlAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseControlActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.search_patient)
    TextView searchPatient;
    @BindView(R.id.my_patient_rv)
    RecyclerView myPatientRv;
    CaseControlAdapter caseControlAdapter;
    private List<PatientBean> patientList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_case_control;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CaseControlActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        pageTitle.setText("我的患者");
        patientList = new ArrayList<>();
        //添加自定义分割线
        myPatientRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        myPatientRv.addItemDecoration(divider);
        caseControlAdapter = new CaseControlAdapter(R.layout.case_control_item, patientList);
        myPatientRv.setAdapter(caseControlAdapter);
        init();
    }

    private void init() {
        PatientBean bean = new PatientBean();
        bean.setDiagName("糖尿病");
        bean.setVisitDtime("2018年6月12日");
        bean.setName("贾宝玉");
        bean.setVisitOrgName("浙江糖尿病中心");
        patientList.add(bean);
        caseControlAdapter.notifyDataSetChanged();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.search_patient})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.search_patient:
                Intent intent = new Intent(this, SearchPatientActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
