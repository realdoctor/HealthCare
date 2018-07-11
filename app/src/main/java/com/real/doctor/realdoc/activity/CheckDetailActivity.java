package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CheckDetailActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.check_detail_rv)
    RecyclerView checkDetailRv;
    @BindView(R.id.zip_img)
    ImageView zipImg;
    List<SaveDocBean> mList;
    DocDetailAdapter checkDetailAdapter;
    private String path;

    @Override
    public int getLayoutId() {
        return R.layout.activity_check_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(CheckDetailActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        pageTitle.setText("打包详情");
        zipImg.setVisibility(View.VISIBLE);
        mList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            path = intent.getStringExtra("path");
            mList = intent.getParcelableArrayListExtra("mList");
        }
        //倒序排列
        Collections.reverse(mList);
        //创建布局管理
        checkDetailRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        checkDetailRv.addItemDecoration(divider);
        checkDetailAdapter = new DocDetailAdapter(CheckDetailActivity.this, R.layout.doc_detail_item, mList);
        //给RecyclerView设置适配器
        checkDetailRv.setAdapter(checkDetailAdapter);
    }

    @Override
    public void initEvent() {
        checkDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(CheckDetailActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back, R.id.zip_img})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                //跳转到在线复诊页面
                Intent intent = new Intent(CheckDetailActivity.this, DoctorsListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
            case R.id.zip_img:
                if (EmptyUtils.isNotEmpty(path)) {
                    DocUtils.openAssignFolder(CheckDetailActivity.this, path);
                }
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}
