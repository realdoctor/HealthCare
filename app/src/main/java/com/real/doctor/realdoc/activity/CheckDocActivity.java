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

    private static final int mSaveDocBean_MODE_CHECK = 0;
    private static final int mSaveDocBean_MODE_EDIT = 1;

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.tv_select_num)
    TextView mTvSelectNum;
    @BindView(R.id.btn_delete)
    Button mBtnDelete;
    @BindView(R.id.select_all)
    TextView mSelectAll;
    @BindView(R.id.ll_bottom_dialog)
    LinearLayout mBottomDialog;
    @BindView(R.id.btn_editor)
    TextView mBtnEditor;
    @BindView(R.id.btn_update)
    TextView mBtnUpdate;

    private CheckDocAdapter mCheckDocAdapter = null;
    private LinearLayoutManager mLinearLayoutManager;
    private int mEditMode = mSaveDocBean_MODE_CHECK;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;


    @Override
    public void initEvent() {
        mCheckDocAdapter.setOnItemClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);
        mBtnEditor.setOnClickListener(this);
    }

    @Override
    @OnClick({R.id.btn_delete, R.id.select_all, R.id.btn_editor, R.id.btn_update})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete:
                deleteItem();
                break;
            case R.id.select_all:
                selectAllMain();
                break;
            case R.id.btn_editor:
                updataEditMode();
                break;
            case R.id.btn_update:
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
        intent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    public void doBusiness(Context mContext) {

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
    }

    @Override
    public void initData() {
        mCheckDocAdapter = new CheckDocAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerview.setLayoutManager(mLinearLayoutManager);
        mRecyclerview.setAdapter(mCheckDocAdapter);
        SaveDocManager instance = SaveDocManager.getInstance(CheckDocActivity.this);
        List<SaveDocBean> mList = instance.querySaveDocList(CheckDocActivity.this);
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
            mBtnDelete.setEnabled(true);
            mSelectAll.setText("取消全选");
            isSelectAll = true;
        } else {
            for (int i = 0, j = mCheckDocAdapter.getSaveDocBeanList().size(); i < j; i++) {
                mCheckDocAdapter.getSaveDocBeanList().get(i).setIsSelect(false);
            }
            index = 0;
            mBtnDelete.setEnabled(false);
            mSelectAll.setText("全选");
            isSelectAll = false;
        }
        mCheckDocAdapter.notifyDataSetChanged();
        setBtnBackground(index);
        mTvSelectNum.setText(String.valueOf(index));
    }

    /**
     * 删除逻辑
     */
    private void deleteItem() {
        if (index == 0) {
            mBtnDelete.setEnabled(false);
            return;
        }
        final AlertDialog builder = new AlertDialog.Builder(this)
                .create();
        builder.show();
        if (builder.getWindow() == null) return;
        builder.getWindow().setContentView(R.layout.pop_user);//设置弹出框加载的布局
        TextView msg = (TextView) builder.findViewById(R.id.tv_msg);
        Button cancle = (Button) builder.findViewById(R.id.btn_cancle);
        Button sure = (Button) builder.findViewById(R.id.btn_sure);
        if (msg == null || cancle == null || sure == null) return;

        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = mCheckDocAdapter.getSaveDocBeanList().size(), j = 0; i > j; i--) {
                    SaveDocBean mSaveDocBean = mCheckDocAdapter.getSaveDocBeanList().get(i - 1);
                    if (mSaveDocBean.getIsSelect()) {
                        mCheckDocAdapter.getSaveDocBeanList().remove(mSaveDocBean);
                        index--;
                    }
                }
                index = 0;
                mTvSelectNum.setText(String.valueOf(0));
                setBtnBackground(index);
                if (mCheckDocAdapter.getSaveDocBeanList().size() == 0) {
                    mBottomDialog.setVisibility(View.GONE);
                }
                mCheckDocAdapter.notifyDataSetChanged();
                builder.dismiss();
            }
        });
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
        mSelectAll.setText("全选");
        setBtnBackground(0);
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
                    mSelectAll.setText("取消全选");
                }

            } else {
                mSaveDocBean.setIsSelect(false);
                index--;
                isSelectAll = false;
                mSelectAll.setText("全选");
            }
            setBtnBackground(index);
            mTvSelectNum.setText(String.valueOf(index));
            mCheckDocAdapter.notifyDataSetChanged();
        }
    }
}
