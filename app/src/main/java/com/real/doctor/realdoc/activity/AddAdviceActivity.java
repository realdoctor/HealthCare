package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.KeyBoardUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAdviceActivity extends BaseActivity {

    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.advice)
    EditText advice;
    @BindView(R.id.button_confirm)
    Button buttonConfirm;
    private String pos;
    private String changeAdvice;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_advice;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            pos = intent.getStringExtra("pos");
            changeAdvice = intent.getStringExtra("change");
        }
        if (EmptyUtils.isNotEmpty(changeAdvice)) {
            advice.setText(changeAdvice);
            advice.setSelection(advice.getText().length());
            //更改title
        } else {
            advice.setHint("添加嘱咐");
            //更改title
        }
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.button_confirm, R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm:
                String mAdvice = advice.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("pos", pos);
                intent.putExtra("advice", mAdvice);
                setResult(RESULT_OK, intent);
                //隐藏键盘
                KeyBoardUtils.closeOrOpenKeybord(this);
                finish();
                break;
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
