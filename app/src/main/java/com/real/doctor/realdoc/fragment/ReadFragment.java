package com.real.doctor.realdoc.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ChatPayActivity;
import com.real.doctor.realdoc.activity.NewDetailActivity;
import com.real.doctor.realdoc.activity.RegistrationsActivity;
import com.real.doctor.realdoc.adapter.MultiNewsAdapter;
import com.real.doctor.realdoc.adapter.NewsAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.AdBean;
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

import org.json.JSONArray;
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

import static com.real.doctor.realdoc.adapter.ArticleFragmentAdapter.REFRESH_DATA;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ReadFragment extends BaseFragment implements OnLoadmoreListener, OnRefreshListener, AdapterView.OnItemClickListener {

    @BindView(R.id.lv_news)
    ListView listView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    public MultiNewsAdapter newsAdapter;
    private Unbinder unbinder;
    public ArrayList<Object> newModels = new ArrayList<>();
    public ArrayList<Object> ads = new ArrayList<>();
    private PageModel<NewModel> baseModel = new PageModel<NewModel>();
    public int pageNum = 1;
    public int pageSize = 10;
    public String userId;
    private Dialog mProgressDialog;

    public static ReadFragment newInstance() {
        return new ReadFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_news_list;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        mProgressDialog = DocUtils.getProgressDialog(getActivity(), "正在加载数据....");
    }

    @Override
    public void doBusiness(Context mContext) {
        userId = (String) SPUtils.get(getContext(), Constants.USER_KEY, "");
        newsAdapter = new MultiNewsAdapter(getActivity(), newModels, ads);
        listView.setAdapter(newsAdapter);
        listView.setOnItemClickListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
//        getAdData();
        getData();
        localBroadcast();
    }

    @Override
    public void widgetClick(View v) {

    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(RealDocApplication.getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(REFRESH_DATA);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getData();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getAdData() {
        mProgressDialog.show();
        HashMap<String, Object> params = new HashMap<String, Object>();
        HttpRequestClient.getInstance(getContext()).createBaseApi().get("healthnews/ad/list"
                , params, new BaseObserver<ResponseBody>(getContext()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getActivity(), "获取资讯数据失败!");
                        mProgressDialog.dismiss();
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
                        mProgressDialog.dismiss();
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
                                    JSONArray jsonObject = object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    ads.addAll((ArrayList<Object>) localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<AdBean>>() {
                                            }.getType()));
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取资讯数据失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("userId", userId);
        HttpRequestClient.getInstance(getContext()).createBaseApi().get("healthnews/list"
                , params, new BaseObserver<ResponseBody>(getContext()) {
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
                                    newsAdapter = new MultiNewsAdapter(getActivity(), newModels, ads);
                                    listView.setAdapter(newsAdapter);
                                    newsAdapter.notifyDataSetChanged();
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > newModels.size()) {
            ToastUtil.show(getContext(), "已经是最后一页", Toast.LENGTH_SHORT);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int Type = parent.getAdapter().getItemViewType(position);
        if (Type == MultiNewsAdapter.TYPE_A) {
            NewModel model = (NewModel) parent.getAdapter().getItem(position);
            if (Double.parseDouble(model.price) == 0.00d) {
                Intent intent = new Intent(getContext(), NewDetailActivity.class);
                intent.putExtra("newsId", model.newsId);
                intent.putExtra("focusFlag", model.focusFlag);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), ChatPayActivity.class);
                intent.putExtra("payType", "5");
                intent.putExtra("price", model.price);
                intent.putExtra("focusFlag", model.focusFlag);
                intent.putExtra("newsId", model.newsId);
                startActivity(intent);
            }
        } else if (Type == MultiNewsAdapter.TYPE_B) {

        }
    }
}
