package com.real.doctor.realdoc.activity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.service.UnzipService;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseListActivity extends BaseActivity {

    public static String GET_LIST = "android.intent.action.getList";
    private PatientBean patientBean;
    private String realName;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.inquery)
    TextView inquery;
    @BindView(R.id.inquery_info)
    TextView inqueryInfo;
    @BindView(R.id.line)
    View line;
    DocDetailAdapter docDetailAdapter;
    @BindView(R.id.record_list_recycler)
    RecyclerView recordListRecycleView;
    private List<SaveDocBean> recordList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_case_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CaseListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        patientBean = getIntent().getParcelableExtra("patient");
        realName = getIntent().getStringExtra("realName");
        pageTitle.setText(realName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            Intent startServiceIntent = new Intent(this, UnzipService.class);
            startServiceIntent.putExtra("patientBean", patientBean);
            startService(startServiceIntent);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), UnzipService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
        loadBroadCast();
    }

    private void loadBroadCast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GET_LIST);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //获取患者咨询
                String inqueryText = intent.getExtras().getString("inquery");
                if (EmptyUtils.isNotEmpty(inqueryText)) {
                    inquery.setText(inqueryText);
                    line.setVisibility(View.VISIBLE);
                    inqueryInfo.setVisibility(View.VISIBLE);
                    inqueryInfo.setText("患者" + realName + "咨询的问题:");
                } else {
                    inquery.setVisibility(View.GONE);
                    line.setVisibility(View.GONE);
                    inqueryInfo.setVisibility(View.GONE);
                }
                //获得列表数据
                recordList = intent.getExtras().getParcelableArrayList("list");
                //创建布局管理
                recordListRecycleView.setLayoutManager(new LinearLayoutManager(CaseListActivity.this, LinearLayoutManager.VERTICAL, false));
                //添加自定义分割线
                DividerItemDecoration divider = new DividerItemDecoration(CaseListActivity.this, DividerItemDecoration.VERTICAL);
                divider.setDrawable(ContextCompat.getDrawable(CaseListActivity.this, R.drawable.disease_divider));
                recordListRecycleView.addItemDecoration(divider);
                docDetailAdapter = new DocDetailAdapter(CaseListActivity.this, R.layout.doc_detail_item, recordList);
                //给RecyclerView设置适配器
                recordListRecycleView.setAdapter(docDetailAdapter);
                initListEvent();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    public void initEvent() {
    }

    public void initListEvent() {
        docDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //传过去一个值,删除标题栏上的更改按钮
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(CaseListActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                mBundle.putBoolean("noModify", true);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back})
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
