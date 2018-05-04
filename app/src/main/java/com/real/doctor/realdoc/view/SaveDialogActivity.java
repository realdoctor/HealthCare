package com.real.doctor.realdoc.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.SaveDocActivity;
import com.real.doctor.realdoc.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveDialogActivity extends BaseActivity {

    @BindView(R.id.save_doc_edit)
    EditText saveDocEdit;
    @BindView(R.id.confirm)
    TextView confirm;
    private String path;

    @Override
    public int getLayoutId() {
        return R.layout.save_doc_dialog;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra("mCurrentPhotoPath");
        }
        saveDocEdit.setText("");
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.confirm})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                String advice = saveDocEdit.getText().toString();
                Intent intent = new Intent(this, SaveDocActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("advice", advice);
                bundle.putString("path", path);
                intent.putExtras(bundle);
                SaveDialogActivity.this.setResult(RESULT_OK, intent);
                SaveDialogActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    protected void onDestroy() {
        System.out.println("DialogActivity onDestroy");
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}