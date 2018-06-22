package com.real.doctor.realdoc.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DocDetailAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
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
    @BindView(R.id.right_title)
    TextView rightTitle;
    @BindView(R.id.check_detail_rv)
    RecyclerView checkDetailRv;
    @BindView(R.id.zip_img)
    ImageView zipImg;
    @BindView(R.id.zip_text)
    TextView zipText;
    List<SaveDocBean> mList;
    DocDetailAdapter checkDetailAdapter;
    public static final String GET_PATH = "android.intent.action.get.path";
    public static final int REQUEST_CODE_PROGRESS_BAR = 0x130;
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
        rightTitle.setVisibility(View.VISIBLE);
        rightTitle.setText("上传");
        zipImg.setVisibility(View.GONE);
        zipText.setVisibility(View.GONE);
        mList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null) {
            mList = intent.getParcelableArrayListExtra("mList");
        }
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

    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_title, R.id.zip_img})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
            case R.id.right_title:
                Intent intent = new Intent(CheckDetailActivity.this, ProgressBarActivity.class);
                intent.putParcelableArrayListExtra("mList", (ArrayList<? extends Parcelable>) mList);
                startActivityForResult(intent, REQUEST_CODE_PROGRESS_BAR);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PROGRESS_BAR) {
            zipImg.setVisibility(View.VISIBLE);
            zipText.setVisibility(View.VISIBLE);
            rightTitle.setVisibility(View.GONE);
            path = data.getStringExtra("path");
        }
    }

}
