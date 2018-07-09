package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DoctorBean;
import com.real.doctor.realdoc.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnswerActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.inquery_info)
    TextView inqueryInfo;
    @BindView(R.id.inquery)
    TextView inquery;
    @BindView(R.id.answer_info)
    TextView answerInfo;
    @BindView(R.id.answer)
    TextView answer;
    private DoctorBean doctorBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_answer;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AnswerActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("咨询解答");
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String inqueryText = "";
        String answerText = "";
        String doctorText = "";
        if (intent != null) {
            inqueryText = intent.getStringExtra("inquery");
            answerText = intent.getStringExtra("answer");
            doctorText = intent.getStringExtra("doctor");
        }
        inqueryInfo.setText("您咨询的问题:");
        inquery.setText(inqueryText);
        answerInfo.setText("医生" + doctorText + "的解答:");
        if (answerText.equals("")) {
            answer.setText("该问题医生还未解答!");
        } else {
            answer.setText(answerText);
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
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
