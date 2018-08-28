package com.real.doctor.realdoc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DiseaseListAdapter;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.SizeUtils;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.TriangleDrawable;
import com.real.doctor.realdoc.view.popup.EasyPopup;
import com.real.doctor.realdoc.view.popup.XGravity;
import com.real.doctor.realdoc.view.popup.YGravity;
import com.real.doctor.realdoc.widget.Constant;
import com.real.doctor.realdoc.widget.timepicker.CustomDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class RecordListActivity extends BaseActivity {

    public static final String DATEPICKER_TAG = "datepicker";
    public static String RECORD_LIST_TEXT = "android.intent.action.record.list";
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.title)
    RelativeLayout title;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
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
    DiseaseListAdapter diseaseListAdapter;
    private boolean selectTimeLinearFlag = false;
    private boolean selectDiseaseLinearFlag = false;
    private List<SaveDocBean> recordList;
    private List<String> diseaseList;
    private SaveDocManager instance = null;
    private CustomDatePicker customDatePicker1, customDatePicker2;
    private EasyPopup mRightPop;
    private boolean getList = false;
    private String token;
    private String verifyFlag = "";

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(RecordListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        initDatePicker();
    }

    @Override
    public void initData() {
        token = (String) SPUtils.get(RecordListActivity.this, Constants.TOKEN, "");
        String mobile = (String) SPUtils.get(RecordListActivity.this, Constants.MOBILE, "");
        pageTitle.setText("病历列表");
        rightTitle.setVisibility(View.GONE);
        rightTitle.setText("新增");
        rightIcon.setVisibility(View.VISIBLE);
        if (EmptyUtils.isNotEmpty(token)) {
            //实名认证
            checkName(mobile);
        }
        recordList = new ArrayList<>();
        instance = SaveDocManager.getInstance(RecordListActivity.this);
        if (EmptyUtils.isNotEmpty(instance)) {
            recordList = instance.querySaveDocList(RecordListActivity.this);
        }
        //创建布局管理
        recordListlRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        recordListlRecycleView.addItemDecoration(divider);
        docDetailAdapter = new DocDetailAdapter(RecordListActivity.this, R.layout.doc_detail_item, recordList);
        //给RecyclerView设置适配器
        recordListlRecycleView.setAdapter(docDetailAdapter);
        //疾病列表
        diseaseList = instance.queryDiseaseList(RealDocApplication.getDaoSession(RecordListActivity.this));
        //创建布局管理
        selectDiseaseList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
//        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        selectDiseaseList.addItemDecoration(divider);
        ViewGroup.LayoutParams lp = selectDiseaseList.getLayoutParams();
        if (diseaseList.size() > 4) {
            lp.height = SizeUtils.dip2px(this, 30 * 8);
        } else {
            lp.height = SizeUtils.dip2px(this, 30 * diseaseList.size());
        }
        selectDiseaseList.setLayoutParams(lp);
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
        initAbovePop();
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
                mBundle.putBoolean("key", true);
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

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startTime.setText(now.split(" ")[0]);
        endTime.setText(now.split(" ")[0]);
        customDatePicker1 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startTime.setText(time.split(" ")[0]);
            }
        }, "2010年01月01日 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动
        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endTime.setText(time.split(" ")[0]);
            }
        }, "2010年01月01日 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.setIsLoop(false); // 不允许循环滚动
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

    private void initAbovePop() {
        mRightPop = EasyPopup.create()
                .setContext(this)
                .setContentView(R.layout.right_pop_layout)
                .setAnimationStyle(R.style.RightTopPopAnim)
                .setOnViewListener(new EasyPopup.OnViewListener() {
                    @Override
                    public void initViews(View view) {
                        View arrowView = view.findViewById(R.id.v_arrow);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            arrowView.setBackground(new TriangleDrawable(TriangleDrawable.TOP, Color.parseColor("#ff03b5e5")));
                        }
                    }
                })
                .setFocusAndOutsideEnable(true)
                .apply();
        TextView modify = mRightPop.findViewById(R.id.modify);
        TextView compare = mRightPop.findViewById(R.id.compare);
        modify.setText("新增");
        compare.setText("获取远程病历");
        modify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionStart(RecordListActivity.this, SaveRecordActivity.class);
                mRightPop.dismiss();
            }
        });
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到实名认证页面
                if (EmptyUtils.isEmpty(token)) {
                    Intent intent = new Intent(RecordListActivity.this, LoginActivity.class);
                    intent.putExtra("get_list", true);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(RecordListActivity.this, VerifyActivity.class);
                    intent.putExtra("get_list", true);
                    startActivity(intent);
                    finish();
                }
                mRightPop.dismiss();
            }
        });
    }

    @Override
    @OnClick({R.id.select_time_record, R.id.select_disease_record, R.id.start_time, R.id.end_time, R.id.confirm_btn, R.id.finish_back, R.id.right_title, R.id.right_icon})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.select_time_record:
                if (!selectTimeLinearFlag) {
                    selectTimeLinear.setVisibility(View.VISIBLE);
                    startTime.setText(DateUtil.timeStamp2Date(DateUtil.timeStamp(), "yyyy年MM月dd日 HH:mm"));
                    endTime.setText(DateUtil.timeStamp2Date(DateUtil.timeStamp(), "yyyy年MM月dd日 HH:mm"));
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

            case R.id.start_time:
                customDatePicker1.show(startTime.getText().toString());
                break;

            case R.id.end_time:
                customDatePicker2.show(endTime.getText().toString());
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
            case R.id.right_icon:
                showRightPop(v);
                break;
        }
    }

    private void showRightPop(View view) {
        int offsetX = SizeUtils.dp2px(this, 20) - view.getWidth() / 2;
        int offsetY = (title.getHeight() - view.getHeight()) / 2;
        mRightPop.showAtAnchorView(view, YGravity.BELOW, XGravity.ALIGN_RIGHT, offsetX, offsetY);
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

    private void checkName(String mobilePhone) {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobilePhone);
        HttpRequestClient.getInstance(RecordListActivity.this).createBaseApi().get("user/certification/check"
                , param, new BaseObserver<ResponseBody>(RecordListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RecordListActivity.this, "获取用户信息失败.请确定是否已登录!");
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "verifyFlag")) {
                                        verifyFlag = obj.getString("verifyFlag");
                                        SPUtils.put(RecordListActivity.this, Constants.VERIFYFLAG, verifyFlag);
                                        if (StringUtils.equals(verifyFlag, "1")) {
                                            rightTitle.setVisibility(View.VISIBLE);
                                            rightIcon.setVisibility(View.GONE);
                                        } else {
                                            rightTitle.setVisibility(View.GONE);
                                            rightIcon.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(RecordListActivity.this, "获取用户信息失败.请确定是否已登录!");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}
