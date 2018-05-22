package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.AddLabelAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.AddLabelBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddLabelActivity extends BaseActivity {
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.add_label_recycle_view)
    RecyclerView addLabelRecycleView;
    AddLabelAdapter addLabelAdapter;
    private List<AddLabelBean> labelList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_label;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        labelList = new ArrayList<>();
        String path = "android.resource://" + getApplicationContext().getPackageName() + "/";
        AddLabelBean bean1 = new AddLabelBean();
        bean1.setName("处方标签");
        bean1.setIcon(path + R.mipmap.add);
        labelList.add(bean1);
        AddLabelBean bean2 = new AddLabelBean();
        bean2.setName("医嘱标签");
        bean2.setIcon(path + R.mipmap.bg_healthy);
        labelList.add(bean2);
        AddLabelBean bean3 = new AddLabelBean();
        bean3.setName("体征标签");
        bean3.setIcon(path + R.mipmap.avatar_bg);
        labelList.add(bean3);
        AddLabelBean bean4 = new AddLabelBean();
        bean4.setName("报告检查标签");
        bean4.setIcon(path + R.mipmap.arrow_white);
        labelList.add(bean4);
        addLabelAdapter = new AddLabelAdapter(R.layout.add_label_item, labelList);        //创建布局管理
        addLabelRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加Android自带的分割线
        addLabelRecycleView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //给RecyclerView设置适配器
        addLabelRecycleView.setAdapter(addLabelAdapter);
    }

    @Override
    public void initEvent() {
        addLabelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击item,回调数据

                AddLabelBean bean = (AddLabelBean) adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("addLabelBean", bean);
                setResult(RESULT_OK, intent);
                finish();
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
