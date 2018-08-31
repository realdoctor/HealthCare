package com.real.doctor.realdoc.activity;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.adapter.MultilDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.service.UnzipService;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class CaseListActivity extends BaseActivity {

    public static String GET_LIST = "android.intent.action.getList";
    public static String GET_ANSWER = "android.intent.action.getAnswer";
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.inquery)
    TextView inquery;
    @BindView(R.id.inquery_answer_tv)
    TextView inqueryAnswerTv;
    @BindView(R.id.inquery_one)
    TextView inqueryOne;
    @BindView(R.id.inquery_question_one)
    TextView inqueryQuestionOne;
    @BindView(R.id.inquery_one_tv)
    TextView inqueryOneTv;
    @BindView(R.id.inquery_one_answer_tv)
    TextView inqueryOneAnswerTv;
    @BindView(R.id.inquery_two)
    TextView inqueryTwo;
    @BindView(R.id.inquery_question_two)
    TextView inqueryQuestionTwo;
    @BindView(R.id.inquery_two_tv)
    TextView inqueryTwoTv;
    @BindView(R.id.inquery_two_answer_tv)
    TextView inqueryTwoAnswerTv;
    @BindView(R.id.inquery_three)
    TextView inqueryThree;
    @BindView(R.id.inquery_question_three)
    TextView inqueryQuestionThree;
    @BindView(R.id.inquery_three_tv)
    TextView inqueryThreeTv;
    @BindView(R.id.inquery_three_answer_tv)
    TextView inqueryThreeAnswerTv;
    @BindView(R.id.inquery_info)
    TextView inqueryInfo;
    @BindView(R.id.record_list_title)
    TextView recordListTitle;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.reply)
    Button reply;
    MultilDetailAdapter multilDetailAdapter;
    @BindView(R.id.record_list_recycler)
    RecyclerView recordListRecycleView;
    private List<SaveDocBean> recordList;
    private String disease;
    private String src;
    private String inqueryText;
    private String patientRecordId;
    private String realName;
    private String messageId;
    private String questionId;
    private Dialog mProgressDialog;

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
        mProgressDialog = DocUtils.getProgressDialog(CaseListActivity.this, "正在加载数据....");
    }

    @Override
    public void initData() {
        realName = getIntent().getExtras().getString("realName");
        src = getIntent().getExtras().getString("src");
        disease = getIntent().getExtras().getString("title");
        inqueryText = getIntent().getExtras().getString("question");
        messageId = getIntent().getExtras().getString("messageId");
        patientRecordId = getIntent().getExtras().getString("patientRecordId");
        questionId = getIntent().getExtras().getString("questionId");
        pageTitle.setText(realName);
        //获取患者咨询
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
        //获得问题答案
        getAnswers();
        startService();
        loadBroadCast();
    }

    private void getAnswers() {
        HashMap<String, String> param = new HashMap<>();
        param.put("messageId", messageId);
        HttpRequestClient.getInstance(CaseListActivity.this).createBaseApi().get("askQuestion/reply/info"
                , param, new BaseObserver<ResponseBody>(CaseListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(CaseListActivity.this, "获取该问题答案失败!");
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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONArray jsonArray = object.getJSONArray("data");
                                        int length = jsonArray.length();
                                        if (length == 1) {
                                            JSONObject jsonObj = jsonArray.getJSONObject(0);
                                            if (DocUtils.hasValue(jsonObj, "question")) {
                                                String question = jsonObj.getString("question");
                                                inquery.setVisibility(View.VISIBLE);
                                                inquery.setText(question);
                                            }
                                            if (DocUtils.hasValue(jsonObj, "answer")) {
                                                String answer = jsonObj.getString("answer");
                                                inqueryAnswerTv.setVisibility(View.VISIBLE);
                                                inqueryAnswerTv.setText(answer);
                                            }
                                        } else if (length == 2) {
                                            for (int i = 0; i < length; i++) {
                                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                inqueryOne.setVisibility(View.VISIBLE);
                                                inqueryOneTv.setVisibility(View.VISIBLE);
                                                inqueryTwo.setVisibility(View.VISIBLE);
                                                inqueryTwoTv.setVisibility(View.VISIBLE);
                                                if (i == 0) {
                                                    if (DocUtils.hasValue(jsonObj, "question")) {
                                                        String question = jsonObj.getString("question");
                                                        inqueryQuestionOne.setVisibility(View.VISIBLE);
                                                        inqueryQuestionOne.setText(question);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "answer")) {
                                                        String answer = jsonObj.getString("answer");
                                                        inqueryOneAnswerTv.setVisibility(View.VISIBLE);
                                                        inqueryOneAnswerTv.setText(answer);
                                                    }
                                                } else if (i == 1) {
                                                    if (DocUtils.hasValue(jsonObj, "question")) {
                                                        String question = jsonObj.getString("question");
                                                        inqueryQuestionTwo.setVisibility(View.VISIBLE);
                                                        inqueryQuestionTwo.setText(question);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "answer")) {
                                                        String answer = jsonObj.getString("answer");
                                                        inqueryTwoAnswerTv.setVisibility(View.VISIBLE);
                                                        inqueryTwoAnswerTv.setText(answer);
                                                    }
                                                }
                                            }
                                        } else if (length == 3) {
                                            inqueryOne.setVisibility(View.VISIBLE);
                                            inqueryOneTv.setVisibility(View.VISIBLE);
                                            inqueryTwo.setVisibility(View.VISIBLE);
                                            inqueryTwoTv.setVisibility(View.VISIBLE);
                                            inqueryThree.setVisibility(View.VISIBLE);
                                            inqueryThreeTv.setVisibility(View.VISIBLE);
                                            for (int i = 0; i < length; i++) {
                                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                                if (i == 0) {
                                                    if (DocUtils.hasValue(jsonObj, "question")) {
                                                        String question = jsonObj.getString("question");
                                                        inqueryQuestionOne.setVisibility(View.VISIBLE);
                                                        inqueryQuestionOne.setText(question);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "answer")) {
                                                        String answer = jsonObj.getString("answer");
                                                        inqueryOneAnswerTv.setVisibility(View.VISIBLE);
                                                        inqueryOneAnswerTv.setText(answer);
                                                    }
                                                } else if (i == 1) {
                                                    if (DocUtils.hasValue(jsonObj, "question")) {
                                                        String question = jsonObj.getString("question");
                                                        inqueryQuestionTwo.setVisibility(View.VISIBLE);
                                                        inqueryQuestionTwo.setText(question);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "answer")) {
                                                        String answer = jsonObj.getString("answer");
                                                        inqueryTwoAnswerTv.setVisibility(View.VISIBLE);
                                                        inqueryTwoAnswerTv.setText(answer);
                                                    }
                                                } else if (i == 2) {
                                                    if (DocUtils.hasValue(jsonObj, "question")) {
                                                        String question = jsonObj.getString("question");
                                                        inqueryQuestionThree.setVisibility(View.VISIBLE);
                                                        inqueryQuestionThree.setText(question);
                                                    }
                                                    if (DocUtils.hasValue(jsonObj, "answer")) {
                                                        String answer = jsonObj.getString("answer");
                                                        inqueryThreeAnswerTv.setVisibility(View.VISIBLE);
                                                        inqueryThreeAnswerTv.setText(answer);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(CaseListActivity.this, "获取该问题答案失败!");
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

    private void loadBroadCast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GET_LIST);
        intentFilter.addAction(GET_ANSWER);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(GET_LIST)) {
                    //获得列表数据
                    if (EmptyUtils.isEmpty(intent.getExtras())) {
                        line.setVisibility(View.GONE);
                        recordListTitle.setVisibility(View.GONE);
                        mProgressDialog.dismiss();
                        return;
                    }
                    recordList = intent.getExtras().getParcelableArrayList("list");
                    if (EmptyUtils.isNotEmpty(recordList)) {
                        List<SaveDocBean> mList = new ArrayList<>();
                        List<SaveDocBean> oneList = new ArrayList<>();
                        List<SaveDocBean> twoList = new ArrayList<>();
                        int i;
                        for (i = 0; i < recordList.size(); i++) {
                            if (disease.equals(recordList.get(i).getIll())) {
                                oneList.add(recordList.get(i));
                            } else {
                                twoList.add(recordList.get(i));
                            }
                        }
                        mList.addAll(oneList);
                        if (oneList.size() > 0 && twoList.size() > 0) {
                            SaveDocBean bean = new SaveDocBean();
                            bean.setType(2);
                            mList.add(bean);
                        }
                        mList.addAll(twoList);
                        //创建布局管理
                        recordListRecycleView.setLayoutManager(new LinearLayoutManager(CaseListActivity.this, LinearLayoutManager.VERTICAL, false));
                        //添加自定义分割线
                        DividerItemDecoration divider = new DividerItemDecoration(CaseListActivity.this, DividerItemDecoration.VERTICAL);
                        divider.setDrawable(ContextCompat.getDrawable(CaseListActivity.this, R.drawable.disease_divider));
                        recordListRecycleView.addItemDecoration(divider);
                        //创建适配器
                        multilDetailAdapter = new MultilDetailAdapter(mList);
                        //给RecyclerView设置适配器
                        recordListRecycleView.setAdapter(multilDetailAdapter);
                        initListEvent();
                        mProgressDialog.dismiss();
                    }
                } else if (action.equals(GET_ANSWER)) {
                    getAnswers();
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressDialog.show();
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            Intent startServiceIntent = new Intent(this, UnzipService.class);
            startServiceIntent.putExtra("src", src);
            startService(startServiceIntent);
            JobInfo jobInfo = new JobInfo.Builder(1, new ComponentName(getPackageName(), UnzipService.class.getName()))
                    .setPeriodic(2000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
        }
    }

    @Override
    public void initEvent() {
    }

    public void initListEvent() {
        multilDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //传过去一个值,删除标题栏上的更改按钮
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(CaseListActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                mBundle.putBoolean("noModify", true);
                mBundle.putBoolean("key", false);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back, R.id.reply})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.reply:
                //点击回复按钮
                Intent intent = new Intent(CaseListActivity.this, ReplyActivity.class);
                intent.putExtra("questionId", questionId);
                intent.putExtra("patientRecordId", patientRecordId);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
