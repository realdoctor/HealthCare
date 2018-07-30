package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.ReadFragment;
import com.real.doctor.realdoc.fragment.ReadResultFragment;
import com.real.doctor.realdoc.util.ScreenUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/23.
 */

public class SearchInfoResultActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView page_title;
    public String searchKey;
    @Override
    public int getLayoutId() {
        return R.layout.activity_search_info_result;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SearchInfoResultActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        searchKey=(String)getIntent().getStringExtra("searchKey");
    }

    @Override
    public void initData() {
        page_title.setText("搜索结果");
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_show, ReadResultFragment.newInstance(searchKey)).commit();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick(R.id.finish_back)
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                SearchInfoResultActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
