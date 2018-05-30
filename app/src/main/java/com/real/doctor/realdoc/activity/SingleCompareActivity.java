package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.SingleCompareAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleCompareActivity extends BaseActivity implements SingleCompareAdapter.OnItemClickListener {

    private static final int mSaveDocBean_MODE_CHECK = 0;
    private static final int mSaveDocBean_MODE_EDIT = 1;

    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.btn_editor)
    TextView mBtnEditor;
    @BindView(R.id.btn_compare)
    TextView mBtnCompare;
    private boolean editorStatus = false;
    private int mEditMode = mSaveDocBean_MODE_CHECK;
    private SingleCompareAdapter mSingleCompareAdapter = null;
    private List<SaveDocBean> mList;
    private SaveDocBean mSaveDocBean;
    private List<SaveDocBean> mSaveList;


    @Override
    public int getLayoutId() {
        return R.layout.activity_single_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        pageTitle.setText("选择病历");
        mSaveList = new ArrayList<>();
        Intent intent = getIntent();
        SaveDocBean saveDocBean = null;
        if (intent != null) {
            saveDocBean = intent.getParcelableExtra("saveDocBean");
            mSaveList.add(saveDocBean);
        }
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SaveDocManager instance = SaveDocManager.getInstance(SingleCompareActivity.this);
        mList = instance.querySaveDocList(SingleCompareActivity.this,saveDocBean.getId());
        for(int i= 0;i<mList.size();i++){
            mList.get(i).setIsSelect(false);
        }
        mSaveList.add(mList.get(0));
        mSingleCompareAdapter = new SingleCompareAdapter(this, mRecyclerview, mList);
        mRecyclerview.setAdapter(mSingleCompareAdapter);
    }

    @Override
    public void initEvent() {
        mSingleCompareAdapter.setOnItemClickListener(this);
    }

    @Override
    @OnClick({R.id.btn_editor, R.id.btn_compare})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_editor:
                updataEditMode();
                break;
            case R.id.btn_compare:
                if (mSaveList.size() == 2) {
                    Intent mIntent = new Intent(this, RecordCompareActivity.class);
                    mIntent.putParcelableArrayListExtra("mSaveList", (ArrayList<? extends Parcelable>) mSaveList);
                    startActivity(mIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
        }
    }

    private void updataEditMode() {
        mEditMode = mEditMode == mSaveDocBean_MODE_CHECK ? mSaveDocBean_MODE_EDIT : mSaveDocBean_MODE_CHECK;
        if (mEditMode == mSaveDocBean_MODE_EDIT) {
            mBtnEditor.setText("取消");
            editorStatus = true;
        } else {
            mBtnEditor.setText("编辑");
            editorStatus = false;
        }
        mSingleCompareAdapter.setEditMode(mEditMode);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onItemClickListener(int pos) {
        if (editorStatus) {
            mSaveList.remove(1);
            mSaveDocBean = mList.get(pos);
            mSaveList.add(mSaveDocBean);
        }
    }
}
