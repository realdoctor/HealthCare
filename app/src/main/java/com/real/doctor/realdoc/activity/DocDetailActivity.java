package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDetailActivity extends BaseActivity {

    DocDetailAdapter docDetailAdapter;
//    @BindView(R.id.doc_detail_recycler)
    RecyclerView docDetailRecycleView;


    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_detail;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        SaveDocManager instance = SaveDocManager.getInstance(DocDetailActivity.this);
        List<SaveDocBean> list = instance.querySaveDocList(DocDetailActivity.this);
        docDetailRecycleView = findViewById(R.id.doc_detail_recycler);
        //创建布局管理
        docDetailRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        //创建适配器
        docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this,R.layout.doc_detail_item, list);
        //给RecyclerView设置适配器
        docDetailRecycleView.setAdapter(docDetailAdapter);
    }

    @Override
    public void initEvent() {
        docDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
