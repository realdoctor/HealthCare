package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CheckCompareAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/27.
 */

public class CheckCompareActivity extends BaseActivity implements CheckCompareAdapter.OnItemClickListener {

    private static final int mSaveDocBean_MODE_CHECK = 0;
    private static final int mSaveDocBean_MODE_EDIT = 1;

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.btn_editor)
    TextView mBtnEditor;
    @BindView(R.id.btn_compare)
    TextView mBtnCompare;

    private CheckCompareAdapter mCheckDocAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private int mEditMode = mSaveDocBean_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;
    private List<SaveDocBean> mList;
    private List<SaveDocBean> mSelectList;
    private List<Integer> isSelectList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CheckCompareActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        isSelectList = new ArrayList<>();
        mSelectList = new ArrayList<>();
        mCheckDocAdapter = new CheckCompareAdapter(this, mRecyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mCheckDocAdapter);
        SaveDocManager instance = SaveDocManager.getInstance(CheckCompareActivity.this);
        mList = instance.querySaveDocList(CheckCompareActivity.this);
        //将所有已选中去除
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getIsSelect()) {
                mList.get(i).setIsSelect(false);
            }
        }
        mCheckDocAdapter.notifyAdapter(mList, false);
    }

    @Override
    public void initEvent() {
        mBtnEditor.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.btn_editor, R.id.btn_compare})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_editor:
                updataEditMode();
                break;
            case R.id.btn_compare:
                //跳转到病历对比页面
                isSelectList = mCheckDocAdapter.getSelectedList();
                if (isSelectList.size() == 2) {
                    mSelectList.add(mList.get(isSelectList.get(0)));
                    mSelectList.add(mList.get(isSelectList.get(1)));
                    Intent mIntent = new Intent(this, DocCompareActivity.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putParcelableArrayList("mSelectList", (ArrayList<? extends Parcelable>) mSelectList);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                    mSelectList.clear();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    ToastUtil.showLong(this, "请选择两份病历,才能进行比较!");
                }
                break;
            default:
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
        mCheckDocAdapter.setEditMode(mEditMode);
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onItemClickListener(int pos, List<SaveDocBean> mSaveDocBeanList) {

        if (editorStatus) {
            SaveDocBean mSaveDocBean = mSaveDocBeanList.get(pos);
            boolean isSelect = mSaveDocBean.getIsSelect();
            if (!isSelect) {
                index++;
                mSaveDocBean.setIsSelect(true);
                if (index == mSaveDocBeanList.size()) {
                    isSelectAll = true;
                }

            } else {
                mSaveDocBean.setIsSelect(false);
                index--;
                isSelectAll = false;
            }
            mCheckDocAdapter.notifyDataSetChanged();
        }
    }
}
