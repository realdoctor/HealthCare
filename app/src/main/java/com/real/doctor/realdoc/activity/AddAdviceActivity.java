package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.VideoManager;
import com.real.doctor.realdoc.model.AddLabelBean;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.KeyBoardUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddAdviceActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.advice)
    EditText advice;
    @BindView(R.id.add_label_relative)
    RelativeLayout addLabelRelative;
    @BindView(R.id.add_label_btn)
    Button addLabelBtn;
    @BindView(R.id.label_text_relative)
    RelativeLayout labelTextRelative;
    @BindView(R.id.label_text)
    TextView labelText;
    @BindView(R.id.label_icon)
    ImageView labelIcon;
    @BindView(R.id.button_confirm)
    Button buttonConfirm;
    private AddLabelBean addLabelBean;
    private String pos;
    private String changeAdvice;
    private boolean label;
    private VideoBean bean;
    private VideoManager instance;
    //标签添加
    private static final int REQUEST_CODE_ADD_LABEL = 0x100;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_advice;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AddAdviceActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        instance = VideoManager.getInstance(this);
        Intent intent = getIntent();
        if (intent != null) {
            pos = intent.getStringExtra("pos");
            changeAdvice = intent.getStringExtra("change");
            bean = intent.getParcelableExtra("video");
            label = intent.getBooleanExtra("label", false);
        }
        if (label) {
            addLabelRelative.setVisibility(View.VISIBLE);
        } else {
            addLabelRelative.setVisibility(View.GONE);
        }
        if (EmptyUtils.isNotEmpty(changeAdvice)) {
            advice.setText(changeAdvice);
            advice.setSelection(advice.getText().length());
        } else {
            advice.setHint("添加备注");
        }
        pageTitle.setText("添加备注");
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.button_confirm, R.id.finish_back, R.id.add_label_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm:
                String mAdvice = advice.getText().toString().trim();
                if (EmptyUtils.isNotEmpty(bean)) {
                    Intent intent = new Intent();
                    bean.setAdvice(mAdvice);
                    int spare;
                    String name = "";
                    if (EmptyUtils.isNotEmpty(addLabelBean)) {
                        name = addLabelBean.getName();
                    }
                    if (StringUtils.equals(name, "处方")) {
                        spare = 1;
                    } else if (StringUtils.equals(name, "医嘱")) {
                        spare = 2;
                    } else if (StringUtils.equals(name, "体征")) {
                        spare = 3;
                    } else if (StringUtils.equals(name, "报告检查")) {
                        spare = 4;
                    } else {
                        spare = 0;
                    }
                    bean.setSpareImage(spare);
                    instance.insertVideo(AddAdviceActivity.this, bean);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    //音频添加描述也用同一段代码
                    Intent intent = new Intent();
                    intent.putExtra("pos", pos);
                    if (EmptyUtils.isNotEmpty(addLabelBean)) {
                        intent.putExtra("addLabelBean", addLabelBean);
                    }
                    intent.putExtra("advice", mAdvice);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            case R.id.add_label_btn:
                //点击添加标签按钮
                Intent intent = new Intent(this, AddLabelActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_LABEL);
                break;
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADD_LABEL) {
            //添加标签
            labelTextRelative.setVisibility(View.VISIBLE);
            addLabelBean = data.getParcelableExtra("addLabelBean");
            labelText.setText(addLabelBean.getName());
            String icon = addLabelBean.getIcon();
            if (EmptyUtils.isNotEmpty(icon)) {
                Glide.with(AddAdviceActivity.this).load(icon).crossFade().into(labelIcon);
            }
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
