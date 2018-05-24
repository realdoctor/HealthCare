package com.real.doctor.realdoc.activity;

import com.fourmob.datetimepicker.date.DatePickerDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DiseaseListAdapter;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordListActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    public static final String DATEPICKER_TAG = "datepicker";
    public static String RECORD_LIST_TEXT = "android.intent.action.record.list";
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    DocDetailAdapter docDetailAdapter;
    @BindView(R.id.record_list_recycler)
    RecyclerView recordListlRecycleView;
    @BindView(R.id.select_time_record)
    TextView selectTimeRecord;
    @BindView(R.id.select_disease_record)
    TextView selectDiseaseRecord;
    @BindView(R.id.start_time)
    TextView startTime;
    @BindView(R.id.end_time)
    TextView endTime;
    @BindView(R.id.add_start_time)
    ImageView addStartTime;
    @BindView(R.id.add_end_time)
    ImageView addEndTime;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.select_time_linear)
    LinearLayout selectTimeLinear;
    @BindView(R.id.select_disease_linear)
    LinearLayout selectDiseaseLinear;
    @BindView(R.id.select_disease_list)
    RecyclerView selectDiseaseList;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    DatePickerDialog datePickerDialog;
    DiseaseListAdapter diseaseListAdapter;
    private boolean startFlag = false;
    private boolean endFlag = false;
    private boolean selectTimeLinearFlag = false;
    private boolean selectDiseaseLinearFlag = false;
    private List<SaveDocBean> recordList;
    private List<String> diseaseList;
    private SaveDocManager instance = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("新增");
        recordList = new ArrayList<>();
        instance = SaveDocManager.getInstance(RecordListActivity.this);
        if (EmptyUtils.isNotEmpty(instance)) {
            recordList = instance.querySaveDocList(RecordListActivity.this);
        }
        //创建布局管理
        recordListlRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加Android自带的分割线
        recordListlRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
        //给RecyclerView设置适配器
        recordListlRecycleView.setAdapter(docDetailAdapter);
        final Calendar calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), true);
        //疾病列表
        diseaseList = instance.queryDiseaseList(RealDocApplication.getDaoSession(RecordListActivity.this));
        //创建布局管理
        selectDiseaseList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加Android自带的分割线
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        selectDiseaseList.addItemDecoration(divider);
//        selectDiseaseList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        diseaseListAdapter = new DiseaseListAdapter(RecordListActivity.this, R.layout.disease_list_item, diseaseList);
        selectDiseaseList.setAdapter(diseaseListAdapter);
        localBroadcast();

        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                //处理刷新列表逻辑
                refreshList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void initEvent() {
        docDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(RecordListActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        diseaseListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //模糊查询包含该疾病的数据
                List<SaveDocBean> list = instance.queryRecordByDiseaseList(RecordListActivity.this, diseaseList.get(position));
                if (EmptyUtils.isNotEmpty(list)) {
                    docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, list);
                    //给RecyclerView设置适配器
                    recordListlRecycleView.setAdapter(docDetailAdapter);
                    initEvent();
                }
                selectDiseaseLinear.setVisibility(View.GONE);
                selectDiseaseLinearFlag = false;
                selectTimeLinearFlag = true;
            }
        });
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
                // 加载完数据设置为不刷新状态，将下拉进度收起来
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_LIST_TEXT);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (EmptyUtils.isNotEmpty(instance)) {
                    recordList = instance.querySaveDocList(RecordListActivity.this);
                    //docDetailAdapter.notifyDataSetChanged();不好使
                    docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
                    //给RecyclerView设置适配器
                    recordListlRecycleView.setAdapter(docDetailAdapter);
                    initEvent();
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    @OnClick({R.id.select_time_record, R.id.select_disease_record, R.id.add_start_time, R.id.add_end_time, R.id.confirm_btn, R.id.finish_back, R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.select_time_record:
                if (!selectTimeLinearFlag) {
                    selectTimeLinear.setVisibility(View.VISIBLE);
                    startTime.setText(DateUtil.timeStamp2Date(DateUtil.timeStamp(), "y年M月d日"));
                    endTime.setText(DateUtil.timeStamp2Date(DateUtil.timeStamp(), "y年M月d日"));
                    selectTimeLinear.clearAnimation();
                    selectDiseaseLinear.setVisibility(View.GONE);
                    selectDiseaseLinearFlag = false;
                    selectTimeLinearFlag = true;
                } else {
                    selectTimeLinear.clearAnimation();
                    selectTimeLinear.setVisibility(View.GONE);
                    selectTimeLinearFlag = false;
                    selectDiseaseLinearFlag = true;
                }
                break;

            case R.id.select_disease_record:
                if (!selectDiseaseLinearFlag) {
                    selectDiseaseLinear.setVisibility(View.VISIBLE);
                    selectTimeLinear.setVisibility(View.GONE);
                    selectTimeLinearFlag = false;
                    selectDiseaseLinearFlag = true;
                } else {
                    selectDiseaseLinear.clearAnimation();
                    selectDiseaseLinear.setVisibility(View.GONE);
                    selectDiseaseLinearFlag = false;
                    selectTimeLinearFlag = true;
                }
                break;

            case R.id.add_start_time:
                startFlag = true;
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                break;

            case R.id.add_end_time:
                endFlag = true;
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                break;

            case R.id.confirm_btn:
                selectTimeLinear.setVisibility(View.GONE);
                String start = startTime.getText().toString().trim();
                String end = endTime.getText().toString().trim();
                getDateList(start, end);
                break;
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_title:
                actionStart(this, SaveRecordActivity.class);
                break;
        }
    }

    public void refreshList() {
        if (EmptyUtils.isNotEmpty(instance)) {
            recordList = instance.querySaveDocList(RecordListActivity.this);
            //docDetailAdapter.notifyDataSetChanged();不好使
            docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
            //给RecyclerView设置适配器
            recordListlRecycleView.setAdapter(docDetailAdapter);
            initEvent();
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        if (startFlag && !endFlag) {
            startTime.setText(year + "年" + (month + 1) + "月" + day + "日");
            startFlag = false;
        } else if (!startFlag && endFlag) {
            endTime.setText(year + "年" + (month + 1) + "月" + day + "日");
            endFlag = false;
        }
    }

    private void getDateList(String start, String end) {
        String startTime = DateUtil.date2TimeStamp(start, "yyyy年MM月dd日");
        String endTime = DateUtil.date2TimeStamp(end, "yyyy年MM月dd日");
        if (Long.valueOf(startTime) > Long.valueOf(endTime)) {
            ToastUtil.showLong(this, "开始时间不能大于结束时间!");
            return;
        }
        if (EmptyUtils.isNotEmpty(instance)) {
            recordList = instance.queryRecordByTimeList(RecordListActivity.this, startTime, endTime);
            if (recordList.size() == 0) {
                ToastUtil.showLong(this, "该时间段不存在您所需要的病历!");
                docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
                //给RecyclerView设置适配器
                recordListlRecycleView.setAdapter(docDetailAdapter);
                return;

            } else {
                docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
                //给RecyclerView设置适配器
                recordListlRecycleView.setAdapter(docDetailAdapter);
                initEvent();
            }
        }
    }
}
