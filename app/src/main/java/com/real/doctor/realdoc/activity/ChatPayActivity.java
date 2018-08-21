package com.real.doctor.realdoc.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.PayResult;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ChatPayActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.zhi_fu_bao_linear)
    LinearLayout zhiFuBaoLinear;
    @BindView(R.id.weixin_linear)
    LinearLayout weixinLinear;
    @BindView(R.id.social_security_linear)
    LinearLayout socialSecurityLinear;
    @BindView(R.id.zhi_fu_bao)
    CheckBox rbAlipay;
    @BindView(R.id.wei_xin)
    CheckBox rbWechat;
    @BindView(R.id.social_security)
    CheckBox socialSecurity;
    @BindView(R.id.tv_count_price)
    TextView tvCountprice;
    @BindView(R.id.bt_pay)
    TextView btPay;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    //zhifu_type=0支付宝支付,zhifu_type=1微信支付
    private String zhifu_type = "0";
    private static final int SDK_PAY_FLAG = 0;
    private IWXAPI api;
    private String payType;
    private String doctorUserId;
    private String patientRecordId;
    private String desease;
    private boolean detail;
    private String userId;
    private String price;
    private String newsId;
    private String focusFlag;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat_pay;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ChatPayActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        payType = getIntent().getStringExtra("payType");
        doctorUserId = getIntent().getStringExtra("doctorUserId");
        desease = getIntent().getStringExtra("desease");
        patientRecordId = getIntent().getStringExtra("patientRecordId");
        detail = getIntent().getBooleanExtra("detail", false);
        focusFlag = getIntent().getStringExtra("focusFlag");
        if (payType.equals("1")) {
            pageTitle.setText("聊天咨询支付");
            initGetPay();
        } else if (payType.equals("2")) {
            pageTitle.setText("病历咨询支付");
            initGetPay();
        } else if (payType.equals("5")) {
            pageTitle.setText("资讯支付");
            price = getIntent().getStringExtra("price");
            newsId = getIntent().getStringExtra("newsId");
            tvCountprice.setText(price);
        }
        userId = (String) SPUtils.get(ChatPayActivity.this, Constants.USER_KEY, "");
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        api.registerApp(Constants.WX_APP_ID);
    }

    private void initGetPay() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", doctorUserId);
        map.put("patientRecordId", patientRecordId);
        HttpRequestClient.getInstance(ChatPayActivity.this).createBaseApi().get("askQuestion/getAskQuestionMoney"
                , map, new BaseObserver<ResponseBody>(ChatPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(ChatPayActivity.this, "获取价格失败!");
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
                        String msg = "";
                        String code = "";
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
                                    if (payType.equals("1")) {
                                        if (DocUtils.hasValue(obj, "chatMoney")) {
                                            String chatMoney = obj.getString("chatMoney");
                                            tvCountprice.setText(chatMoney);
                                        }
                                    } else if (payType.equals("2")) {
                                        if (DocUtils.hasValue(obj, "questionMoney")) {
                                            String questionMoney = obj.getString("questionMoney");
                                            tvCountprice.setText(questionMoney);
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(ChatPayActivity.this, msg);
                                }
                            } catch (JSONException e) {
                                if (e.getMessage().equals("Value null at data of type org.json.JSONObject$1 cannot be converted to JSONObject")) {
                                    ToastUtil.showLong(ChatPayActivity.this, "该医生还未填写复诊咨询报价!");
                                    ChatPayActivity.this.finish();
                                }
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void initEvent() {
        rbAlipay.setOnCheckedChangeListener(this);
        rbWechat.setOnCheckedChangeListener(this);
        socialSecurity.setOnCheckedChangeListener(this);
    }

    private boolean isWXAppInstalledAndSupported(Context context,
                                                 IWXAPI api) {
        // LogOutput.d(TAG, "isWXAppInstalledAndSupported");
        boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
                && api.isWXAppSupportAPI();
        if (!sIsWXAppInstalledAndSupported) {
            ToastUtil.showLong(this, "尚未安装微信客户端或者微信版本不支持");
        }
        return sIsWXAppInstalledAndSupported;
    }

    @Override
    @OnClick({R.id.bt_pay, R.id.finish_back, R.id.zhi_fu_bao_linear, R.id.weixin_linear, R.id.social_security_linear})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay:
                if (zhifu_type.length() == 0) {
                    ToastUtil.showLong(this, "请选择支付方式");
                    return;
                } else {
                    if (zhifu_type.equals("0")) {
                        payOrderByAlipay();
                    } else if (zhifu_type.equals("1")) {
                        if (isWXAppInstalledAndSupported(ChatPayActivity.this, api)) {
                            payOrderByWechat();
                        }
                    }
                }
                break;
            case R.id.finish_back:
                ChatPayActivity.this.finish();
                break;
            case R.id.zhi_fu_bao_linear:
                zhifu_type = "0";
                rbWechat.setChecked(false);
                rbAlipay.setChecked(true);
                socialSecurity.setChecked(false);
                break;
            case R.id.weixin_linear:
                zhifu_type = "1";
                rbAlipay.setChecked(false);
                rbWechat.setChecked(true);
                socialSecurity.setChecked(false);
                break;
            case R.id.social_security_linear:
                rbAlipay.setChecked(false);
                rbWechat.setChecked(false);
                socialSecurity.setChecked(true);
                break;
        }
    }

    public void payOrderByAlipay() {
        JSONObject json = new JSONObject();
        try {
            if (payType.equals("1")) {
                json.put("from", "1");
            } else if (payType.equals("2")) {
                json.put("from", "2");
            } else if (payType.equals("5")) {
                json.put("from", "5");
            }
            json.put("userId", userId);
            if (payType.equals("1") || payType.equals("2")) {
                json.put("goodsId", patientRecordId);
            } else if (payType.equals("5")) {
                json.put("goodsId", newsId);
            }
            json.put("toUserId", doctorUserId);
            json.put("type", "alipay");
            json.put("payAmount", tvCountprice.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(ChatPayActivity.this).createBaseApi().json("pay/orderPayT/"
                , body, new BaseObserver<ResponseBody>(ChatPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(ChatPayActivity.this, "支付宝支付失败!");
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
                                    ToastUtil.showLong(ChatPayActivity.this, "支付宝支付成功!");
//                                    JSONObject orderObject = object.getJSONObject("data");
//                                    final String orderInfo = orderObject.getString("orderString");
//                                    Runnable payRunnable = new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            // 构造PayTask 对象
//                                            PayTask alipay = new PayTask(ChatPayActivity.this);
//                                            // 调用支付接口，获取支付结果
//                                            String result = alipay.pay(orderInfo, true);
//
//                                            Message msg = new Message();
//                                            msg.what = SDK_PAY_FLAG;
//                                            msg.obj = result;
//                                            mHandler.sendMessage(msg);
//                                        }
//                                    };
//                                    // 必须异步调用
//                                    Thread payThread = new Thread(payRunnable);
//                                    payThread.start();
                                    goToNextStep();
                                } else {
                                    ToastUtil.showLong(ChatPayActivity.this, "支付宝支付失败!");
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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        ToastUtil.showLong(ChatPayActivity.this, "支付成功");
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            ToastUtil.showLong(ChatPayActivity.this, "支付结果确认中");
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            ToastUtil.showLong(ChatPayActivity.this, "支付失败");
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    public void payOrderByWechat() {
        JSONObject json = new JSONObject();
        try {
            if (payType.equals("1")) {
                json.put("from", "1");
            } else if (payType.equals("2")) {
                json.put("from", "2");
            } else if (payType.equals("5")) {
                json.put("from", "5");
            }
            json.put("userId", userId);
            if (payType.equals("1") || payType.equals("2")) {
                json.put("goodsId", patientRecordId);
            } else if (payType.equals("5")) {
                json.put("goodsId", newsId);
            }
            json.put("toUserId", doctorUserId);
            json.put("type", "wxpay");
            json.put("payAmount", tvCountprice.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json.toString());
        HttpRequestClient.getInstance(ChatPayActivity.this).createBaseApi().json("pay/orderPayT/"
                , body, new BaseObserver<ResponseBody>(ChatPayActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(ChatPayActivity.this, "微信支付失败!");
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
                                    ToastUtil.showLong(ChatPayActivity.this, "微信支付成功!");
//                                    String pay_str = object.getString("rs");
//                                    JSONObject ob = new JSONObject(pay_str);
//                                    String prepayId = ob.getString("prepayid");
//                                    String nonceStr = ob.getString("noncestr");
//                                    String timeStamp = ob.getString("timestamp");
//                                    String packageValue = ob.getString("package");
//                                    String sign = ob.getString("sign");
//                                    String partnerId = ob.getString("partnerid");
//                                    PayReq req = new PayReq();
//                                    req.appId = Constants.WX_APP_ID;
//                                    req.partnerId = partnerId;
//                                    req.prepayId = prepayId;
//                                    req.nonceStr = nonceStr;
//                                    req.timeStamp = String.valueOf(timeStamp);
//                                    req.packageValue = packageValue;
//                                    req.sign = sign;
//                                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                                    boolean flag = api.sendReq(req);
//                                    if (flag) {
//                                        ChatPayActivity.this.finish();
//                                    }
                                    goToNextStep();
                                } else {
                                    ToastUtil.showLong(ChatPayActivity.this, "微信支付失败!");
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView == rbAlipay) {
                zhifu_type = "0";
                rbWechat.setChecked(false);
                rbAlipay.setChecked(true);
                socialSecurity.setChecked(false);
            } else if (buttonView == rbWechat) {
                zhifu_type = "1";
                rbAlipay.setChecked(false);
                rbWechat.setChecked(true);
                socialSecurity.setChecked(false);
            } else if (buttonView == socialSecurity) {
                rbAlipay.setChecked(false);
                rbWechat.setChecked(false);
                socialSecurity.setChecked(true);
            }
        }
    }

    private void goToNextStep() {
        if (payType.equals("1")) {
            //点击进入聊天页
            Intent intent = new Intent(ChatPayActivity.this, ChatActivity.class);
            intent.putExtra("userId", "admin");
            intent.putExtra("doctorUserId", doctorUserId);
            intent.putExtra("desease", desease);
            intent.putExtra("patientRecordId", patientRecordId);
            startActivity(intent);
        } else if (payType.equals("2")) {
            Intent intent = new Intent(ChatPayActivity.this, InqueryActivity.class);
            intent.putExtra("doctorUserId", doctorUserId);
            intent.putExtra("desease", desease);
            intent.putExtra("detail", detail);
            intent.putExtra("patientRecordId", patientRecordId);
            startActivity(intent);
        } else if (payType.equals("5")) {
            Intent intent = new Intent(ChatPayActivity.this, NewDetailActivity.class);
            intent.putExtra("newsId", newsId);
            intent.putExtra("focusFlag", focusFlag);
            startActivity(intent);
            finish();
        }
    }
}
