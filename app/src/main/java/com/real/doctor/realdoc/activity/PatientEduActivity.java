package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.VideoListAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.VideoBean;
import com.real.doctor.realdoc.model.VideoListBean;
import com.real.doctor.realdoc.util.DataUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.widget.play.AssistPlayer;
import com.real.doctor.realdoc.widget.play.DataInter;
import com.real.doctor.realdoc.widget.play.ReceiverGroupManager;
import com.real.doctor.realdoc.widget.play.cover.GestureCover;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatientEduActivity extends BaseActivity implements
        VideoListAdapter.OnListListener,
        OnReceiverEventListener, OnPlayerEventListener {

    private List<VideoListBean> items = new ArrayList<>();
    private VideoListAdapter adapter;

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.play_recycler)
    RecyclerView playRecycler;
    @BindView(R.id.list_play_container)
    FrameLayout container;

    private boolean toDetail;
    private boolean isLandScape;

    private ReceiverGroup receiverGroup;

    @Override
    public int getLayoutId() {
        return R.layout.activity_patient_edu;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(PatientEduActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        pageTitle.setText("患者教育");
        playRecycler = findViewById(R.id.play_recycler);
        playRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        AssistPlayer.get().addOnReceiverEventListener(this);
        AssistPlayer.get().addOnPlayerEventListener(this);

        receiverGroup = ReceiverGroupManager.get().getLiteReceiverGroup(this);
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);

        items.addAll(DataUtils.getVideoList());
        adapter = new VideoListAdapter(getApplicationContext(), playRecycler, items);
        adapter.setOnListListener(PatientEduActivity.this);
        playRecycler.setAdapter(adapter);
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

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

    @Override
    public void onBackPressed() {
        if (isLandScape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    private void attachFullScreen() {
        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new GestureCover(this));
        receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
//        if(AssistPlayer.get().isPlaying())
        AssistPlayer.get().play(container, null);
    }

    @Override
    protected void onResume() {
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
    protected void onPause() {
        super.onPause();
        if (!toDetail) {
            AssistPlayer.get().pause();
        }
    }

    private void attachList() {
        if (adapter != null) {
            receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
            receiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
            adapter.getListPlayLogic().attachPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AssistPlayer.get().removeReceiverEventListener(this);
        AssistPlayer.get().removePlayerEventListener(this);
        AssistPlayer.get().destroy();
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                onBackPressed();
                break;
            case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                setRequestedOrientation(isLandScape ?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

}
