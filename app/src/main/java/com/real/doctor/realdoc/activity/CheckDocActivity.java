package com.real.doctor.realdoc.activity;

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
    @BindView(R.id.upload_btn)
    TextView uploadBtn;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    private CheckDocAdapter mCheckDocAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isSelectAll = false;
    private int index = 0;
    private String inquery;
    private String doctorUserId;
    private String desease;
    private String questionId;
    private String patientRecordId;

    @Override
    public void initEvent() {
        mCheckDocAdapter.setOnItemClickListener(this);
        mSelectAll.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.select_all, R.id.upload_btn})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.upload_btn:
                //跳转到进度条页面，并开启Service，上传成功，进度条页面消失
                updateService();
            default:
                break;
        }
    }

    private void updateService() {
        Intent intent = new Intent(CheckDocActivity.this, ProgressBarActivity.class);
        List<SaveDocBean> mList = new ArrayList<>();
        for (int i = mCheckDocAdapter.getSaveDocBeanList().size(), j = 0; i > j; i--) {
            SaveDocBean mSaveDocBean = mCheckDocAdapter.getSaveDocBeanList().get(i - 1);
            if (mSaveDocBean.getIsSelect()) {
                mList.add(mSaveDocBean);
            }
        }
        intent.putExtra("inquery", inquery);
        intent.putExtra("desease", desease);
        intent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
        intent.putExtra("doctorUserId", doctorUserId);
        intent.putExtra("questionId", questionId);
        intent.putExtra("patientRecordId", patientRecordId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        uploadBtn.setText("发送");
    }

    @Override
    public void initData() {
        //获取咨询内容
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            inquery = intent.getExtras().getString("inquery");
            doctorUserId = intent.getExtras().getString("doctorUserId");
            desease = intent.getExtras().getString("desease");
            questionId = intent.getExtras().getString("questionId");
            patientRecordId = intent.getExtras().getString("patientRecordId");
        }
        mCheckDocAdapter = new CheckDocAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mCheckDocAdapter);
        SaveDocManager instance = SaveDocManager.getInstance(CheckDocActivity.this);
        List<SaveDocBean> mList = new ArrayList<>();
        List<SaveDocBean> mListOne = instance.queryRecordByEqDiseaseList(CheckDocActivity.this, desease);
        //在SaveDocBean类中设置默认值为1
//        for (int i = 0; i < mListOne.size(); i++) {
//            mListOne.get(i).setType(1);
//        }
        mList.addAll(mListOne);
        SaveDocBean bean = new SaveDocBean();
        bean.setType(2);
        mList.add(bean);
        List<SaveDocBean> mListThree = instance.queryRecordByNotDiseaseList(CheckDocActivity.this, desease);
        //在SaveDocBean类中设置默认值为1
//        for (int i = 0; i < mListThree.size(); i++) {
//            mListThree.get(i).setType(1);
//        }
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
