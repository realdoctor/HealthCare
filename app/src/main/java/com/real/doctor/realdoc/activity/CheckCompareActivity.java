package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CheckCompareAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;

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

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;
    @BindView(R.id.ll_bottom_dialog)
    LinearLayout mBottomDialog;
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
    private List<SaveDocBean> isSelectList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_compare;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        isSelectList = new ArrayList<>();
        mCheckDocAdapter = new CheckCompareAdapter(this,  mRecyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mCheckDocAdapter);
        SaveDocManager instance = SaveDocManager.getInstance(CheckCompareActivity.this);
        mList = instance.querySaveDocList(CheckCompareActivity.this);
        mCheckDocAdapter.notifyAdapter(mList, false);
    }

    @Override
    public void initEvent() {
//        mCheckDocAdapter.setOnItemClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mBtnEditor.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.btn_delete, R.id.btn_editor, R.id.btn_compare})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
//                deleteItem();
                break;
            case R.id.btn_editor:
                updataEditMode();
                break;
            case R.id.btn_compare:
                //跳转到病历对比页面

            default:
                break;
        }
    }

    /**
     * 根据选择的数量是否为0来判断按钮的是否可点击.
     *
     * @param size
     */
    private void setBtnBackground(int size) {
        if (size != 0) {
            mBtnDelete.setBackgroundResource(R.drawable.button_shape);
            mBtnDelete.setEnabled(true);
            mBtnDelete.setTextColor(Color.WHITE);
        } else {
            mBtnDelete.setBackgroundResource(R.drawable.button_noclickable_shape);
            mBtnDelete.setEnabled(false);
            mBtnDelete.setTextColor(ContextCompat.getColor(this, R.color.appthemecolor));
        }
    }

    private void updataEditMode() {
        mEditMode = mEditMode == mSaveDocBean_MODE_CHECK ? mSaveDocBean_MODE_EDIT : mSaveDocBean_MODE_CHECK;
        if (mEditMode == mSaveDocBean_MODE_EDIT) {
            mBtnEditor.setText("取消");
            mBottomDialog.setVisibility(View.VISIBLE);
            editorStatus = true;
        } else {
            mBtnEditor.setText("编辑");
            mBottomDialog.setVisibility(View.GONE);
            editorStatus = false;
            clearAll();
        }
        mCheckDocAdapter.setEditMode(mEditMode);
    }

    private void clearAll() {
        mTvSelectNum.setText(String.valueOf(0));
        isSelectAll = false;
        setBtnBackground(0);
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
            setBtnBackground(index);
            mTvSelectNum.setText(String.valueOf(index));
            mCheckDocAdapter.notifyDataSetChanged();
        }
    }
}
