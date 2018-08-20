package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
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
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class MyFollowNewsActivity extends BaseActivity implements OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.lv_news)
    RecyclerView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.page_title)
    TextView page_title;

    public NewsAdapter newsAdapter;
    public ArrayList<NewModel> newModels = new ArrayList<NewModel>();
    private PageModel<NewModel> baseModel = new PageModel<NewModel>();
    public int pageNum = 1;
    public int pageSize = 10;
    public String userId;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_follow_news;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(MyFollowNewsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        userId = (String) SPUtils.get(MyFollowNewsActivity.this, Constants.USER_KEY, "");
        page_title.setText("我的关注");
        newsAdapter = new NewsAdapter(R.layout.my_news_item, newModels);
        listView.setAdapter(newsAdapter);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        getData();
    }

    @Override
    public void initEvent() {
        if (EmptyUtils.isNotEmpty(newsAdapter)) {
            newsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    NewModel model = (NewModel) adapter.getItem(position);
                    Intent intent = new Intent(MyFollowNewsActivity.this, NewDetailActivity.class);
                    intent.putExtra("newsId", model.newsId);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                MyFollowNewsActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > newModels.size()) {
            ToastUtil.show(MyFollowNewsActivity.this, "已经是最后一页", Toast.LENGTH_SHORT);
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


    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        HttpRequestClient.getInstance(MyFollowNewsActivity.this).createBaseApi().get("healthnews/myFocusList"
                , params, new BaseObserver<ResponseBody>(MyFollowNewsActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<NewModel>>() {
                                            }.getType());
                                    newModels.addAll(baseModel.list);
                                    newsAdapter.notifyDataSetChanged();
                                } else {

                                }
                                initData();
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
