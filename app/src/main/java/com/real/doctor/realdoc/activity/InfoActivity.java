package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.real.doctor.realdoc.adapter.InfoAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.greendao.table.PushInfoManager;
import com.real.doctor.realdoc.model.PushInfoBean;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InfoActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.info_recycle_view)
    RecyclerView infoRv;
    private String userId;
    InfoAdapter infoAdapter;
    private PushInfoManager instance;
    private List<PushInfoBean> pushInfoList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_info;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(InfoActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        pageTitle.setText("系统消息");
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("清除");
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(InfoActivity.this, Constants.USER_KEY, "");
        //创建布局管理
        infoRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.disease_divider));
        infoRv.addItemDecoration(divider);
        instance = PushInfoManager.getInstance(InfoActivity.this);
        pushInfoList = instance.queryPushInfoList(InfoActivity.this);
        infoAdapter = new InfoAdapter(R.layout.info_item, pushInfoList);
        //给RecyclerView设置适配器
        infoRv.setAdapter(infoAdapter);
    }

    @Override
    public void initEvent() {
        infoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PushInfoBean bean = (PushInfoBean) adapter.getItem(position);
                String tagId = bean.getTabId();
                if (tagId.equals("0")) {
                    //病人,当病人接收到医生的回复后,跳转到我的复诊界面看答案
                    Intent i = new Intent(InfoActivity.this, MyRevisitActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else if (tagId.equals("1")) {
                    //医生,当病人上传了病历文件后,通知医生到患者管理界面
                    Intent i = new Intent(InfoActivity.this, CaseControlActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else if (tagId.equals("2")) {
                    //跳转到聊天界面
                    Intent i = new Intent(InfoActivity.this, ChatActivity.class);
                    //i.putExtra("str", str);
                    //此处必须这么填,为了参数对应
                    i.putExtra("userId", bean.getFromMobile());
                    i.putExtra("doctorUserId", bean.getFromUserId());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                InfoActivity.this.finish();
                break;
            case R.id.right_title:
                //点击清除
                instance.deleteAllPushInfo(this);
                pushInfoList.clear();
                infoAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
