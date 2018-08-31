package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.MyFollowNewsActivity;
import com.real.doctor.realdoc.activity.NewDetailActivity;
import com.real.doctor.realdoc.adapter.ArticleFragmentAdapter;
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.NewModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
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
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/18.
 */

public class MyFollowNewsFragment extends BaseFragment implements OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.lv_news)
    RecyclerView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    public NewsAdapter newsAdapter;
    public ArrayList<NewModel> newModels = new ArrayList<NewModel>();
    private PageModel<NewModel> baseModel = new PageModel<NewModel>();
    public int pageNum = 1;
    public int pageSize = 10;
    public String userId;
    private Unbinder unbinder;
    private boolean isUserIn = false;

    public static MyFollowNewsFragment newInstance() {
        return new MyFollowNewsFragment();
    }

    public void getFragData(String userId) {
        this.userId = userId;
        getData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_follow_news;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {
        initData();
    }

    public void initData() {
        listView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        newsAdapter = new NewsAdapter(R.layout.my_news_item, newModels);
        listView.setAdapter(newsAdapter);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        userId = (String) SPUtils.get(getActivity(), Constants.USER_KEY, "");
        getData();
    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        //发送广播，关闭悬浮窗
        if (isUserIn) {
            Intent msgIntent = new Intent(HomeFragment.CLOSE_WINDOW_MANAGER);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
            isUserIn = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > newModels.size()) {
            ToastUtil.show(getActivity(), "已经是最后一页", Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        getData();
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum = 1;
        newModels.clear();
        getData();
        refreshLayout.finishRefresh();
    }


    public void initEvent() {
        if (EmptyUtils.isNotEmpty(newsAdapter)) {
            newsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    isUserIn = true;
                    NewModel model = (NewModel) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), NewDetailActivity.class);
                    intent.putExtra("newsId", model.newsId);
                    startActivity(intent);
                }
            });
        }
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        HttpRequestClient.getInstance(RealDocApplication.getContext()).createBaseApi().get("healthnews/focus/list"
                , params, new BaseObserver<ResponseBody>(RealDocApplication.getContext()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(RealDocApplication.getContext(), "获取关注数据列表失败!");
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    if(EmptyUtils.isNotEmpty(baseModel.list)){
                                        baseModel.list.clear();
                                        newModels.clear();
                                    }
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<NewModel>>() {
                                            }.getType());
                                    newModels.addAll(baseModel.list);
                                    if (EmptyUtils.isEmpty(newsAdapter)) {
                                        newsAdapter = new NewsAdapter(R.layout.my_news_item, newModels);
                                        listView.setAdapter(newsAdapter);
                                    } else {
                                        newsAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    ToastUtil.showLong(RealDocApplication.getContext(), "获取关注数据列表失败!");
                                }
                                initEvent();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

}
