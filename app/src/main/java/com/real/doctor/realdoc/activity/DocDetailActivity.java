package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.view.RecyclerViewSpacesItemDecoration;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/24.
 */

public class DocDetailActivity extends BaseActivity {

    DocDetailAdapter docDetailAdapter;
    @BindView(R.id.doc_detail_recycler)
    RecyclerView docDetailRecycleView;
    @BindView(R.id.finish_back)
    ImageView finishBack;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doc_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        SaveDocManager instance = SaveDocManager.getInstance(DocDetailActivity.this);
        List<SaveDocBean> list = instance.querySaveDocList(DocDetailActivity.this);
        docDetailRecycleView = findViewById(R.id.doc_detail_recycler);
        //创建布局管理
        docDetailRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//bottom间距
        docDetailRecycleView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        //创建适配器
        docDetailAdapter = new DocDetailAdapter(DocDetailActivity.this, R.layout.doc_detail_item, list);
        //给RecyclerView设置适配器
        docDetailRecycleView.setAdapter(docDetailAdapter);
    }

    @Override
    public void initEvent() {
        docDetailAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(DocDetailActivity.this, DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                intent.putExtras(mBundle);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
