package com.real.doctor.realdoc.fragment;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.CaseControlActivity;
import com.real.doctor.realdoc.activity.ChatActivity;
import com.real.doctor.realdoc.activity.DoctorsListActivity;
import com.real.doctor.realdoc.activity.InfoActivity;
import com.real.doctor.realdoc.activity.LoginActivity;
import com.real.doctor.realdoc.activity.MyQrActivity;
import com.real.doctor.realdoc.activity.MyRevisitActivity;
import com.real.doctor.realdoc.activity.PatientEduListActivity;
import com.real.doctor.realdoc.activity.DocContentActivity;
import com.real.doctor.realdoc.activity.RecordListActivity;
import com.real.doctor.realdoc.activity.RegistrationsActivity;
import com.real.doctor.realdoc.activity.ScannerActivity;
import com.real.doctor.realdoc.activity.SearchHistoryListActivity;
import com.real.doctor.realdoc.activity.VerifyActivity;
import com.real.doctor.realdoc.adapter.HomeRecordAdapter;
import com.real.doctor.realdoc.application.RealDocApplication;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.SaveDocBean;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.StringUtils;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.floatmenu.FloatBallManager;
import com.real.doctor.realdoc.view.floatmenu.floatball.FloatBallCfg;
import com.real.doctor.realdoc.view.floatmenu.utils.DensityUtil;
import com.real.doctor.realdoc.widget.permission.FloatPermissionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABannerUtil;

import static com.real.doctor.realdoc.activity.RecordListActivity.RECORD_LIST_TEXT;
import static com.real.doctor.realdoc.fragment.UserFragment.VERIFY_TEXT;

/**
 * user：lqm
 * desc：第一个模块，主页Fragment
 */

public class HomeFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.home_search)
    RelativeLayout homeSearch;
    @BindView(R.id.save_doc_linear)
    LinearLayout saveDocLinear;
    @BindView(R.id.appoint_icon)
    LinearLayout appointIconLinear;
    @BindView(R.id.doctor_online)
    LinearLayout doctorOnline;
    @BindView(R.id.search_text)
    TextView searchText;
    @BindView(R.id.case_control)
    LinearLayout caseControl;
    @BindView(R.id.patient_education)
    LinearLayout patientEducation;
    @BindView(R.id.telemedicine)
    LinearLayout telemedicine;
    @BindView(R.id.my_qr)
    LinearLayout myWQr;
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.bga_banner)
    BGABanner bgaBanner;
    @BindView(R.id.scan_icon)
    ImageView scanIcon;
    @BindView(R.id.title_linear)
    LinearLayout titleLinear;
    @BindView(R.id.main_function_layout)
    LinearLayout mainLayout;
    @BindView(R.id.doc_function_layout)
    LinearLayout docLayout;
    @BindView(R.id.viewFlipper)
    ViewFlipper viewFlipper;
    @BindView(R.id.info_icon)
    ImageView infoIcon;
    @BindView(R.id.info_red_icon)
    TextView infoRedInfo;
    TextView comment;
    TextView commentTime;
    RelativeLayout commentRelative;
    TextView connect;
    TextView connectTime;
    RelativeLayout connectRelative;
    private HomeRecordAdapter adapter;
    private SaveDocManager instance = null;
    private String token;
    private List<SaveDocBean> recordList;
    private DividerItemDecoration divider;
    private FloatBallManager mFloatballManager;
    private FloatPermissionManager mFloatPermissionManager;
    private ActivityLifeCycleListener mActivityLifeCycleListener = new ActivityLifeCycleListener();
    private int resumed;
    private boolean btnFlag = true;
    private boolean personFlag = true;
    //该标识用来识别是从首页进入其他页面的,这样就可以处理返回回来按钮显示或不显示的问题
    private boolean isHomeIn = false;
    private String verifyFlag = "";
    private boolean isFirst = true;
    private String isRole;
    private View mView;
    private String mobile;
    private String fromMobile;
    private String fromUserId;
    //    public static String SHOW_RED_ICON = "android.intent.action.show.red.icon";
    public static String SHOW_BOAST_INFO = "android.intent.action.show.boast.info";
    public static String SHOW_WINDOW_ICON = "android.intent.action.show.window.icon";
    public static String CLOSE_WINDOW_MANAGER = "android.intent.action.close.window.manager";

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_home;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        //滚动栏使用
        mView = view;
        mobile = (String) SPUtils.get(getActivity(), Constants.MOBILE, "");
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleLinear.getLayoutParams();
            lp.topMargin = statusHeight;
            titleLinear.setLayoutParams(lp);
        }
        searchText.getBackground().setAlpha(180);
        isRole = (String) SPUtils.get(getActivity(), Constants.ROLE_ID, "");
        if (isRole.equals("1")) {
            mainLayout.setVisibility(View.GONE);
            docLayout.setVisibility(View.VISIBLE);
            personFlag = false;
            initFloatMenu();
        } else if (isRole.equals("0")) {
            mainLayout.setVisibility(View.VISIBLE);
            docLayout.setVisibility(View.GONE);
            personFlag = true;
        }
        initViewFlipper();
        String commentMobile = (String) SPUtils.get(getActivity(), Constants.COMMENT_MOBILE, "");
        if (mobile.equals(commentMobile)) {
            String commentStr = (String) SPUtils.get(getActivity(), Constants.COMMENT, "");
            String commentTimeStr = (String) SPUtils.get(getActivity(), Constants.COMMENT_TIME, "");
            commentRelative = mView.findViewById(R.id.comment_relative);
            comment = mView.findViewById(R.id.comment);
            commentTime = mView.findViewById(R.id.comment_time);
            comment.setText(commentStr);
            commentTime.setText(commentTimeStr);
            initCommentEvent();
        }
        String tagId = (String) SPUtils.get(getActivity(), Constants.CONNECT_TAG_ID, "");
        if (EmptyUtils.isNotEmpty(tagId)) {
            connectRelative = mView.findViewById(R.id.connect_relative);
            connect = mView.findViewById(R.id.connect);
            connectTime = mView.findViewById(R.id.connect_time);
            String connectStr = (String) SPUtils.get(getActivity(), Constants.CONNECT, "");
            String connectTimeStr = (String) SPUtils.get(getActivity(), Constants.CONNECT_TIME, "");
            connect.setText(connectStr);
            connectTime.setText(connectTimeStr);
            initConnectEvent(tagId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showBroadcast();
    }

    private void showBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECORD_LIST_TEXT);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(RECORD_LIST_TEXT)) {
                    if (EmptyUtils.isNotEmpty(instance)) {
                        if (EmptyUtils.isNotEmpty(recycleView)) {
                            recordList = instance.querySaveDocList(getActivity());
                            adapter = new HomeRecordAdapter(R.layout.home_record_item, recordList);
                            recycleView.setAdapter(adapter);
                            initEvent();
                        }
                    }
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    public void initViewFlipper() {
        viewFlipper.addView(View.inflate(getActivity(), R.layout.home_ads, null));
    }

    @Override
    public void doBusiness(Context mContext) {
        verifyFlag = (String) SPUtils.get(getActivity(), Constants.VERIFYFLAG, "");
        //滚轮
        List<View> views = new ArrayList<>();
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.mipmap.useravator_bg));
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.mipmap.login_bg));
        views.add(BGABannerUtil.getItemImageView(getActivity(), R.mipmap.bg_healthy));
        bgaBanner.setData(views);
        bgaBanner.setDelegate(new BGABanner.Delegate<ImageView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, String model, int position) {
                ToastUtil.showLong(banner.getContext(), "点击了" + position);
            }
        });
        token = (String) SPUtils.get(getActivity(), Constants.TOKEN, "");
        if (EmptyUtils.isNotEmpty(token)) {
            if (verifyFlag.equals("1")) {
                instance = SaveDocManager.getInstance(getActivity());
                if (EmptyUtils.isNotEmpty(instance)) {
                    recordList = instance.querySaveDocList(getActivity());
                    recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                    //添加分割线
                    divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
                    divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.disease_divider));
                    recycleView.addItemDecoration(divider);
                    adapter = new HomeRecordAdapter(R.layout.home_record_item, recordList);
                    recycleView.setAdapter(adapter);
                }
                initEvent();
            }
        }
        localBroadcast();
    }

    private void initFloatMenu() {
        isFirst = (boolean) SPUtils.get(getActivity(), "first", true);
        //1 初始化悬浮球配置，定义好悬浮球大小和icon的drawable
        final int ballSize = DensityUtil.dip2px(getActivity(), 60);
        Drawable ballIcon;
        if (personFlag) {
            ballIcon = getActivity().getResources().getDrawable(R.mipmap.change_icon);
        } else {
            ballIcon = getActivity().getResources().getDrawable(R.mipmap.change_icon_checked);
        }
        //可以尝试使用以下几种不同的config。
        final FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER, 450);
        mFloatballManager = new FloatBallManager(RealDocApplication.getContext(), ballCfg);
        setFloatPermission();
//        SPUtils.put(getActivity(), "first", false);
        //5 如果没有添加菜单，可以设置悬浮球点击事件
        if (mFloatballManager.getMenuItemSize() == 0) {
            mFloatballManager.setOnFloatBallClickListener(new FloatBallManager.OnFloatBallClickListener() {
                @Override
                public void onFloatBallClick() {
                    if (personFlag) {
                        personFlag = false;
                        SPUtils.put(getActivity(), Constants.ROLE_CHANGE_ID, "1");
                        final Drawable ballIcon = getActivity().getResources().getDrawable(R.mipmap.change_icon_checked);
                        final FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER, 450);
                        mFloatballManager.changeIcon(getActivity(), ballCfg);
                        docLayout.setVisibility(View.VISIBLE);
                        mainLayout.setVisibility(View.GONE);
                    } else {
                        personFlag = true;
                        SPUtils.put(getActivity(), Constants.ROLE_CHANGE_ID, "0");
                        final Drawable ballIcon = getActivity().getResources().getDrawable(R.mipmap.change_icon);
                        final FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER, 450);
                        mFloatballManager.changeIcon(getActivity(), ballCfg);
                        mainLayout.setVisibility(View.VISIBLE);
                        docLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
        //6 如果想做成应用内悬浮球，可以添加以下代码。
        getActivity().getApplication().registerActivityLifecycleCallbacks(mActivityLifeCycleListener);
    }

    private void setFloatPermission() {
        // 设置悬浮球权限，用于申请悬浮球权限的，这里用的是别人写好的库，可以自己选择
        //如果不设置permission，则不会弹出悬浮球
        mFloatPermissionManager = new FloatPermissionManager();
        mFloatballManager.setPermission(new FloatBallManager.IFloatBallPermission() {
            @Override
            public boolean onRequestFloatBallPermission() {
                if (isFirst) {
                    requestFloatBallPermission(getActivity());
                }
                return true;
            }

            @Override
            public boolean hasFloatBallPermission(Context context) {
                boolean permission = mFloatPermissionManager.checkPermission(context);
                return permission;
            }

            @Override
            public void requestFloatBallPermission(Activity activity) {
                if (isFirst) {
                    mFloatPermissionManager.applyPermission(activity);
                    isFirst = false;
                }
            }
        });
    }

    public class ActivityLifeCycleListener implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ++resumed;
            if (btnFlag) {
                setFloatballVisible(true);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            --resumed;
            if (!isApplicationInForeground()) {
                if (btnFlag) {
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }
    }

    private void setFloatballVisible(boolean visible) {
        if (visible) {
            mFloatballManager.show();
        } else {
            mFloatballManager.hide();
        }
    }

    public boolean isApplicationInForeground() {
        return resumed > 0;
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VERIFY_TEXT);
        intentFilter.addAction(SHOW_BOAST_INFO);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(VERIFY_TEXT)) {
                    instance = SaveDocManager.getInstance(getActivity());
                    verifyFlag = (String) SPUtils.get(getActivity(), Constants.VERIFYFLAG, "");
                    if (StringUtils.equals(verifyFlag, "1")) {
                        recordList();
                    }
                } else if (action.equals(SHOW_BOAST_INFO)) {
                    //显示红色标记
                    if (EmptyUtils.isNotEmpty(infoRedInfo)) {
                        infoRedInfo.setVisibility(View.VISIBLE);
                    }
                    String info = null;
                    String tagId = null;
                    String time = null;
                    String fromMobile = null;
                    String fromUserId = null;
                    if (intent != null && intent.getExtras() != null) {
                        info = intent.getExtras().getString("info");
                        tagId = intent.getExtras().getString("tagId");
                        time = intent.getExtras().getString("time");
                        fromMobile = intent.getExtras().getString("fromMobile");
                        fromUserId = intent.getExtras().getString("userId");
                    }
                    if (tagId.equals("0")) {
                        connectRelative = mView.findViewById(R.id.connect_relative);
                        connect = mView.findViewById(R.id.connect);
                        connectTime = mView.findViewById(R.id.connect_time);
                        connect.setText(info);
                        connectTime.setText(time);
                        SPUtils.put(getActivity(), Constants.CONNECT, info);
                        SPUtils.put(getActivity(), Constants.CONNECT_TIME, time);
                        SPUtils.put(getActivity(), Constants.CONNECT_TAG_ID, tagId);
                        initConnectEvent(tagId);
                    } else if (tagId.equals("1")) {
                        connectRelative = mView.findViewById(R.id.connect_relative);
                        connect = mView.findViewById(R.id.connect);
                        connectTime = mView.findViewById(R.id.connect_time);
                        connect.setText(info);
                        connectTime.setText(time);
                        SPUtils.put(getActivity(), Constants.CONNECT, info);
                        SPUtils.put(getActivity(), Constants.CONNECT_TIME, time);
                        SPUtils.put(getActivity(), Constants.CONNECT_TAG_ID, tagId);
                        initConnectEvent(tagId);
                    } else if (tagId.equals("2")) {
                        commentRelative = mView.findViewById(R.id.comment_relative);
                        comment = mView.findViewById(R.id.comment);
                        commentTime = mView.findViewById(R.id.comment_time);
                        comment.setText(info);
                        commentTime.setText(time);
                        //需要存储起来,下次显示，如果未登录,则不显示
                        SPUtils.put(getActivity(), Constants.COMMENT, info);
                        SPUtils.put(getActivity(), Constants.COMMENT_TIME, time);
                        SPUtils.put(getActivity(), Constants.COMMENT_FROM_MOBILE, fromMobile);
                        SPUtils.put(getActivity(), Constants.COMMENT_MOBILE, mobile);
                        SPUtils.put(getActivity(), Constants.COMMENT_USER_ID, fromUserId);
                        initCommentEvent();
                    }
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void initEvent() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                isHomeIn = true;
                //点击item事件
                SaveDocBean bean = (SaveDocBean) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), DocContentActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("SaveDocBean", bean);
                mBundle.putBoolean("key", true);
                intent.putExtras(mBundle);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void initCommentEvent() {
        if (EmptyUtils.isNotEmpty(commentRelative)) {
            commentRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击进入聊天界面
                    //医生,当病人上传了病历文件后,通知医生到患者管理界面
                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    //此处必须这么填,为了参数对应
                    fromMobile = (String) SPUtils.get(getActivity(), Constants.COMMENT_FROM_MOBILE, "");
                    fromUserId = (String) SPUtils.get(getActivity(), Constants.COMMENT_USER_ID, "");
                    i.putExtra("userId", fromMobile);
                    i.putExtra("doctorUserId", fromUserId);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(i);
                }
            });
        }
    }

    private void initConnectEvent(final String tagId) {
        if (EmptyUtils.isNotEmpty(connectRelative)) {
            connectRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tagId.equals("0")) {
                        //病人,当病人接收到医生的回复后,跳转到我的复诊界面看答案
                        Intent i = new Intent(getActivity(), MyRevisitActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(i);
                    } else if (tagId.equals("1")) {
                        //医生,当病人上传了病历文件后,通知医生到患者管理界面
                        Intent i = new Intent(getActivity(), CaseControlActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(i);
                    }
                }
            });
        }
    }

    private void recordList() {
        if (EmptyUtils.isNotEmpty(instance)) {
            recordList = instance.querySaveDocList(getActivity());
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            //添加分割线
            divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
//                    recycleView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
            divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.disease_divider));
            recycleView.addItemDecoration(divider);
            adapter = new HomeRecordAdapter(R.layout.home_record_item, recordList);
            recycleView.setAdapter(adapter);
            initEvent();
        }
    }

    @OnClick({R.id.home_search, R.id.save_doc_linear, R.id.base_cure, R.id.doctor_online, R.id.scan_icon, R.id.appoint_icon, R.id.case_control, R.id.patient_education, R.id.telemedicine, R.id.my_qr, R.id.info_icon})
    @Override
    public void widgetClick(View v) {
        if (DocUtils.isFastClick()) {
            Intent intent;
            switch (v.getId()) {
                case R.id.home_search:
                    isHomeIn = true;
                    intent = new Intent(getActivity(), SearchHistoryListActivity.class);
                    intent.putExtra("requestCode", RegistrationsActivity.REGISTRATION_EVENT_REQUEST_CODE);
                    startActivity(intent);
                    break;
                case R.id.save_doc_linear:
                    isHomeIn = true;
                    intent = new Intent(getActivity(), RecordListActivity.class);
                    startActivity(intent);
                    break;
                case R.id.base_cure:
                    isHomeIn = true;
                    break;
                case R.id.doctor_online:
                    if (NetworkUtil.isNetworkAvailable(getActivity())) {
                        if (EmptyUtils.isNotEmpty(token)) {
                            if (verifyFlag.equals("1")) {
                                isHomeIn = true;
                                //在线复诊
                                intent = new Intent(getActivity(), DoctorsListActivity.class);
                                startActivity(intent);
                            } else {
                                //跳转到实名认证页面
                                intent = new Intent(getActivity(), VerifyActivity.class);
                                startActivity(intent);
                            }
                        } else {
                            intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            ToastUtil.showLong(getActivity(), "请登录您的账户!");
                        }
                    } else {
                        ToastUtil.showLong(getActivity(), "您还未连接网络,请连接互联网!");
                        NetworkUtil.goToWifiSetting(getActivity());
                    }
                    break;
                case R.id.appoint_icon:
                    isHomeIn = true;
                    if (NetworkUtil.isNetworkAvailable(getActivity())) {
                        intent = new Intent(getActivity(), RegistrationsActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.showLong(getActivity(), "您还未连接网络,请连接互联网!");
                        NetworkUtil.goToWifiSetting(getActivity());
                    }
                    break;
                case R.id.scan_icon:
                    isHomeIn = true;
                    intent = new Intent(getActivity(), ScannerActivity.class);
                    startActivity(intent);
                    break;
                case R.id.case_control:
                    if (EmptyUtils.isNotEmpty(token)) {
                        isHomeIn = true;
                        intent = new Intent(getActivity(), CaseControlActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        ToastUtil.showLong(getActivity(), "请登录您的账户!");
                    }
                    break;
                case R.id.patient_education:
                    isHomeIn = true;
                    //intent = new Intent(getActivity(), PatientEduActivity.class);
                    intent = new Intent(getActivity(), PatientEduListActivity.class);
                    intent.putExtra("id", "");
                    startActivity(intent);
                    break;
                case R.id.telemedicine:
                    break;
                case R.id.my_qr:
                    if (EmptyUtils.isNotEmpty(token)) {
                        isHomeIn = true;
                        intent = new Intent(getActivity(), MyQrActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        ToastUtil.showLong(getActivity(), "请登录您的账户!");
                    }
                    break;
                case R.id.info_icon:
                    if (EmptyUtils.isNotEmpty(token)) {
                        isHomeIn = true;
                        //红色标记消失
                        if (EmptyUtils.isNotEmpty(infoRedInfo)) {
                            infoRedInfo.setVisibility(View.GONE);
                        }
                        intent = new Intent(getActivity(), InfoActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        ToastUtil.showLong(getActivity(), "请登录您的账户!");
                    }
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        btnFlag = true;
        if (isHomeIn) {
            onShowMenu();
            isHomeIn = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        btnFlag = false;
        onDestroyMenu();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (EmptyUtils.isNotEmpty(mFloatballManager)) {
            mFloatballManager.hide();
        }
        //注册ActivityLifeCyclelistener以后要记得注销，以防内存泄漏。
        getActivity().getApplication().unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener);
    }

    public void onShowMenu() {
        if (EmptyUtils.isNotEmpty(mFloatballManager)) {
            mFloatballManager.show();
            //如果想做成应用内悬浮球，可以添加以下代码。
            getActivity().getApplication().registerActivityLifecycleCallbacks(mActivityLifeCycleListener);
        }
    }

    public void onDestroyMenu() {
        if (EmptyUtils.isNotEmpty(mFloatballManager)) {
            mFloatballManager.hide();
            //注册ActivityLifeCyclelistener以后要记得注销，以防内存泄漏。
            getActivity().getApplication().unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener);
        }
    }
}