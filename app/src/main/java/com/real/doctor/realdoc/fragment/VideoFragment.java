package com.real.doctor.realdoc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.NewDetailActivity;
import com.real.doctor.realdoc.activity.PatientEduActivity;
import com.real.doctor.realdoc.adapter.InfoAdapter;
import com.real.doctor.realdoc.adapter.VideoListAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.InfoModel;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.VideoListBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.play.AssistPlayer;
import com.real.doctor.realdoc.widget.play.DataInter;
import com.real.doctor.realdoc.widget.play.ReceiverGroupManager;
import com.real.doctor.realdoc.widget.play.cover.GestureCover;
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

public class VideoFragment extends BaseFragment implements OnLoadmoreListener,OnRefreshListener, VideoListAdapter.OnListListener,
        OnReceiverEventListener, OnPlayerEventListener {

    @BindView(R.id.play_recycler)
    RecyclerView playRecycler;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.list_play_container)
    FrameLayout container;
    public VideoListAdapter newsAdapter;
    private Unbinder unbinder;
    public ArrayList<VideoListBean> newModels=new ArrayList<VideoListBean>();
    private PageModel<InfoModel> baseModel = new PageModel<InfoModel>();
    public int pageNum=1;
    public int pageSize=10;
    public String userId;
    private boolean toDetail;
    private boolean isLandScape;
    private Dialog mProgressDialog;
    private ReceiverGroup receiverGroup;

    public static VideoFragment newInstance(String id) {
        VideoFragment infoFragment=new VideoFragment();
        Bundle bundel = new Bundle();
        bundel.putSerializable("id", id);
        infoFragment.setArguments(bundel);
        return infoFragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_video_list;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (getArguments() != null) {
            String id = (String) getArguments().get("id");
            if (id.length() != 0) {
                userId = id;
            } else {
                userId = (String) SPUtils.get(getContext(), Constants.USER_KEY, "");
            }
            playRecycler.setLayoutManager(
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            AssistPlayer.get().addOnReceiverEventListener(this);
            AssistPlayer.get().addOnPlayerEventListener(this);
            receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(getActivity());
            receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
            newsAdapter = new VideoListAdapter(getActivity(), playRecycler, newModels);
            newsAdapter.setOnListListener(this);
            playRecycler.setAdapter(newsAdapter);
            ClassicsHeader header = (ClassicsHeader) refreshLayout.getRefreshHeader();
            ClassicsFooter footer = (ClassicsFooter) refreshLayout.getRefreshFooter();
            refreshLayout.setOnLoadmoreListener(this);
            refreshLayout.setOnRefreshListener(this);
            mProgressDialog = DocUtils.getProgressDialog(getActivity(), "正在加载数据....");
            getData();
        }
    }

    @Override
    public void widgetClick(View v) {

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("type", "1");
        params.put("userId", userId);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("news_pub/list"
                , params, new BaseObserver<ResponseBody>(getActivity()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        ToastUtil.showLong(getActivity(), "获取视频列表失败!");
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
                                            new TypeToken<PageModel<InfoModel>>() {
                                            }.getType());
                                    for (InfoModel model : baseModel.list) {
                                        VideoListBean bean = new VideoListBean(model.content, model.pic, model.src);
                                        newModels.add(bean);
                                    }
                                    newsAdapter.notifyDataSetChanged();
                                    ToastUtil.showLong(getActivity(), "获取视频列表成功!");
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取视频列表失败!");
                                }
                                mProgressDialog.dismiss();
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
        if(pageSize*pageNum>newModels.size()){
            ToastUtil.show(getActivity(),"已经是最后一页", Toast.LENGTH_SHORT);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            attachFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            attachList();
        }
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

//    @Override
//    public void onBackPressed() {
//        if (isLandScape) {
//            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return;
//        }
//        super.onBackPressed();
//    }

    private void attachFullScreen() {
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new GestureCover(getActivity()));
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
//        if(AssistPlayer.get().isPlaying())
        AssistPlayer.get().play(container, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        toDetail = false;
        AssistPlayer.get().setReceiverGroup(receiverGroup);
        if (isLandScape) {
            attachFullScreen();
        } else {
            attachList();
        }
        AssistPlayer.get().resume();
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!toDetail) {
            AssistPlayer.get().pause();
        }
    }

    private void attachList() {
        if (newsAdapter != null) {
            receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            newsAdapter.getListPlayLogic().attachPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                getActivity().onBackPressed();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                getActivity().setRequestedOrientation(isLandScape ?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }
}
