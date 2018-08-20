package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.AccountActivity;
import com.real.doctor.realdoc.activity.NewDetailActivity;
import com.real.doctor.realdoc.activity.SearchHistoryListActivity;
import com.real.doctor.realdoc.adapter.ArticleFragmentAdapter;
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

import static com.real.doctor.realdoc.util.DpUtils.dip2px;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ArticleShowFragment extends BaseFragment {
    @BindView(R.id.tb_category)
    TabLayout tabLayout;
    @BindView(R.id.vp_show)
    ViewPager viewPager;
    private Unbinder unbinder;
    @BindView(R.id.img_search)
    ImageView img_search;
    @BindView(R.id.tab_bar)
    RelativeLayout tabBar;
    public static final int INFO_SEARCH = 1;
    private ArticleFragmentAdapter articleFragmentAdapter;

    public static ArticleShowFragment newInstance() {
        return new ArticleShowFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.article_show_list;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabBar.getLayoutParams();
            lp.topMargin = statusHeight;
            tabBar.setLayoutParams(lp);
        }
    }


    public void initEvent() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    //do nothing
                } else if (position == 1) {
                    String userId = (String) SPUtils.get(getActivity(), Constants.USER_KEY, "");
                    //点击关注,通知fragment重新获得数据
                    articleFragmentAdapter.getFragmentData(userId);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        articleFragmentAdapter = new ArticleFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(articleFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        initEvent();
    }

    @Override
    @OnClick(R.id.img_search)
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.img_search:
                Intent intent = new Intent(getActivity(), SearchHistoryListActivity.class);
                intent.putExtra("requestCode", INFO_SEARCH);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
