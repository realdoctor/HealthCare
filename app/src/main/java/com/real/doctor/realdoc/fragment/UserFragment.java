package com.real.doctor.realdoc.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.AccountActivity;
import com.real.doctor.realdoc.activity.DoctorsListActivity;
import com.real.doctor.realdoc.activity.LoginActivity;
import com.real.doctor.realdoc.activity.MyRegistrationActivity;
import com.real.doctor.realdoc.activity.OrderListActivity;
import com.real.doctor.realdoc.activity.RecordListActivity;
import com.real.doctor.realdoc.activity.SettingActivity;
import com.real.doctor.realdoc.activity.VerifyActivity;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GlideUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * user：lqm
 * desc：第四个模块，用户模块
 */

public class UserFragment extends BaseFragment {

    private Unbinder unbinder;
    private String token;
    private String mobile;
    private String verifyFlag = "";
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.user_function_one)
    LinearLayout userFunctionOne;
    @BindView(R.id.user_function_two)
    LinearLayout userFunctionTwo;
    @BindView(R.id.user_function_three)
    LinearLayout userFunctionThree;
    @BindView(R.id.user_function_four)
    LinearLayout userFunctionFour;
    @BindView(R.id.user_avator)
    CircleImageView userAvator;
    @BindView(R.id.title)
    RelativeLayout titleRelative;
    @BindView(R.id.page_title)
    TextView pageTitle;
    public static String VERIFY_TEXT = "android.intent.action.record.verify.text";
    private String originalImageUrl = "";

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_user;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        finishBack.setVisibility(View.GONE);
        rightIcon.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rightIcon.setBackground(getActivity().getResources().getDrawable(R.mipmap.icon_settiong, null));
        }
        titleRelative.setBackgroundColor(Color.TRANSPARENT);
        pageTitle.setText("个人中心");
        token = (String) SPUtils.get(getActivity(), "token", "");
        mobile = (String) SPUtils.get(getActivity(), "mobile", "");
        //获得用户信息
        getUserInfo();
        localBroadcast();
    }

    private void localBroadcast() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VERIFY_TEXT);
        intentFilter.addAction(AccountActivity.CHANGE_AVATOR);
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(VERIFY_TEXT)) {
                    getUserInfo();
                } else if (action.equals(AccountActivity.CHANGE_AVATOR)) {
                    originalImageUrl = (String) intent.getExtras().get("avator");
                    GlideUtils.loadImageView(getContext(), originalImageUrl, userAvator);
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);
    }

    private void checkName() {
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobile);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("user/certification/check"
                , param, new BaseObserver<ResponseBody>(getActivity()) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

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
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "verifyFlag")) {
                                        verifyFlag = obj.getString("verifyFlag");
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取用户信息失败.请确定是否已登录!");
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

    private void getUserInfo() {
        //实名认证
        checkName();
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("mobilePhone", mobile);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("user/info"
                , param, new BaseObserver<ResponseBody>(getActivity()) {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

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
                                    JSONObject obj = object.getJSONObject("data");
                                    if (DocUtils.hasValue(obj, "realName")) {
                                        String realName = obj.getString("realName");
                                        if (verifyFlag.equals("1")) {
                                            userName.setText(realName);
                                        } else {
                                            userName.setText("完善信息");
                                        }
                                    }
                                    if (DocUtils.hasValue(obj, "originalImageUrl")) {
                                        originalImageUrl = obj.getString("originalImageUrl");
                                        GlideUtils.loadImageView(getContext(), originalImageUrl, userAvator);
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取用户信息失败.请确定是否已登录!");
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
    public void doBusiness(Context mContext) {

    }

    @Override
    @OnClick({R.id.user_name, R.id.user_function_one, R.id.user_function_two, R.id.user_function_three, R.id.user_function_four, R.id.right_icon})
    public void widgetClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.user_name:
                if (verifyFlag.equals("0")) {
                    //跳转到未实名认证界面
                    intent = new Intent(getActivity(), VerifyActivity.class);
                    startActivity(intent);
                } else if (EmptyUtils.isEmpty(token)) {
                    //跳转到登录界面
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.user_function_one:
                intent = new Intent(getActivity(), MyRegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.user_function_two:
                intent = new Intent(getActivity(), RecordListActivity.class);
                startActivity(intent);
                break;
            case R.id.user_function_three:
                intent = new Intent(getActivity(), DoctorsListActivity.class);
                startActivity(intent);
                break;
            case R.id.user_function_four:
                intent = new Intent(getActivity(), OrderListActivity.class);
                startActivity(intent);
                break;
            case R.id.right_icon:
                //跳转到设置页面`
                intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("imgUrl", originalImageUrl);
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
