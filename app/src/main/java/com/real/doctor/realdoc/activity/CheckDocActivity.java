package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.CheckDocAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujiabin on 2017/9/6.
 */
public class CheckDocActivity extends BaseActivity implements CheckDocAdapter.OnItemClickListener {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.confirm_btn)
    TextView confirmBtn;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    private CheckDocAdapter mCheckDocAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isSelectAll = false;
    private int index = 0;
    private String desease;
    private SaveDocManager instance;
    private Dialog mProgressDialog;

    @Override
    public void initEvent() {
        mCheckDocAdapter.setOnItemClickListener(this);
        mSelectAll.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.select_all, R.id.confirm_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.confirm_btn:
                mProgressDialog.show();
                Intent intent = new Intent();
                List<SaveDocBean> mList = new ArrayList<>();
                List<SaveDocBean> list = new ArrayList<>();
                for (int i = mCheckDocAdapter.getSaveDocBeanList().size(), j = 0; i > j; i--) {
                    SaveDocBean mSaveDocBean = mCheckDocAdapter.getSaveDocBeanList().get(i - 1);
                    if (EmptyUtils.isEmpty(mSaveDocBean.getId())) {
                        continue;
                    } else {
                        if (mSaveDocBean.getIsSelect()) {
                            mList.add(mSaveDocBean);
                        }
                        list.add(mSaveDocBean);
                    }
                }
                instance.updateRecordList(list);
                intent.putParcelableArrayListExtra("records", (ArrayList<? extends Parcelable>) mList);
                setResult(RESULT_OK, intent);
                mProgressDialog.dismiss();
                finish();
            default:
                break;
        }
    }


    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_doc;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CheckDocActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("相关病历资料");
        confirmBtn.setText("确定");
        mProgressDialog = DocUtils.getProgressDialog(CheckDocActivity.this, "正在添加病历数,请稍后....");
    }

    @Override
    public void initData() {
        //获取咨询内容
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            desease = intent.getExtras().getString("desease");
        }
        mCheckDocAdapter = new CheckDocAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mCheckDocAdapter);
        instance = SaveDocManager.getInstance(CheckDocActivity.this);
        List<SaveDocBean> mList = new ArrayList<>();
        List<SaveDocBean> mListOne = instance.queryRecordByEqDiseaseList(CheckDocActivity.this, desease);
        mList.addAll(mListOne);
        SaveDocBean bean = new SaveDocBean();
        bean.setType(2);
        mList.add(bean);
        List<SaveDocBean> mListThree = instance.queryRecordByNotDiseaseList(CheckDocActivity.this, desease);
        mList.addAll(mListThree);
        mCheckDocAdapter.notifyAdapter(mList, false);
    }

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (mCheckDocAdapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = mCheckDocAdapter.getSaveDocBeanList().size(); i < j; i++) {
                mCheckDocAdapter.getSaveDocBeanList().get(i).setIsSelect(true);
            }
            index = mCheckDocAdapter.getSaveDocBeanList().size();
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mCheckDocAdapter.getSaveDocBeanList().size(); i < j; i++) {
                mCheckDocAdapter.getSaveDocBeanList().get(i).setIsSelect(false);
            }
            index = 0;
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mCheckDocAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClickListener(int pos, List<SaveDocBean> mSaveDocBeanList) {
        SaveDocBean mSaveDocBean = mSaveDocBeanList.get(pos);
        boolean isSelect = mSaveDocBean.getIsSelect();
        if (!isSelect) {
            index++;
            mSaveDocBean.setIsSelect(true);
            if (index == mSaveDocBeanList.size()) {
                isSelectAll = true;
                mSelectAll.setText("取消全选");
            }

        } else {
            mSaveDocBean.setIsSelect(false);
            index--;
            isSelectAll = false;
            mSelectAll.setText("全选");
        }
        mCheckDocAdapter.notifyDataSetChanged();
    }
}
