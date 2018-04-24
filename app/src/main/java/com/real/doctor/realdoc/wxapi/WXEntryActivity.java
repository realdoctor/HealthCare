package com.real.doctor.realdoc.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.exception.ExceptionHandle;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.NetworkUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    private BaseResp resp = null;
    private String WX_APP_ID = Constants.WX_APP_ID;
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = Constants.GetCodeRequest;
    // 获取用户个人信息
    private String GetUserInfo = Constants.GetUserInfo;
    private String WX_APP_SECRET = Constants.WX_APP_SECRET;
    private String openid;
    private String access_token;
    private String msg;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        int type = resp.getType();
        if (resp != null) {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    if (type == 1) {
                        final String code = ((SendAuth.Resp) resp).code;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                                        + WX_APP_ID
                                        + "&secret="
                                        + WX_APP_SECRET
                                        + "&code="
                                        + code
                                        + "&grant_type=authorization_code";
                                String data = loginByGet(path);
                                JSONObject object = null;
                                try {
                                    object = new JSONObject(data);
                                    if (DocUtils.hasValue(object, "access_token")) {
                                        access_token = object.getString("access_token");
                                    }
                                    if (DocUtils.hasValue(object, "openid")) {
                                        openid = object.getString("openid");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                getUserMesg(access_token, openid);
                            }
                        }).start();
                    } else if (type == 2) {
                        ToastUtil.showLong(this, "分享成功!");
                    }
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    if (type == 1) {
                        result = "登录取消!";
                    } else if (type == 2) {
                        result = "分享取消!";
                    }
                    ToastUtil.showLong(getApplicationContext(), result);
                    finish();
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    if (type == 1) {
                        result = "登录被拒绝!";
                    } else if (type == 2) {
                        result = "分享被拒绝!";
                    }
                    ToastUtil.showLong(getApplicationContext(), result);
                    finish();
                    break;
                default:
                    if (type == 1) {
                        result = "登录返回!";
                    } else if (type == 2) {
                        result = "分享返回!";
                    }
                    ToastUtil.showLong(getApplicationContext(), result);
                    finish();
                    break;
            }
        }
    }

    /**
     * 获取微信的个人信息
     *
     * @param access_token
     * @param openid
     */
    private void getUserMesg(final String access_token, final String openid) {
        String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid;
        String data = loginByGet(path);
        JSONObject object = null;
        try {
            object = new JSONObject(data);
            String headimgurl = "", nickname = "";
            if (DocUtils.hasValue(object, "nickname")) {
                nickname = object.getString("nickname");
            }
            if (DocUtils.hasValue(object, "headimgurl")) {
                headimgurl = object.getString("headimgurl");
            }
            submitWeixingLogin(access_token, openid, nickname, headimgurl, "wx");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loginByGet(String mUrl) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            // 设置请求的地址 通过URLEncoder.encode(String s, String enc)
            // 使用指定的编码机制将字符串转换为 application/x-www-form-urlencoded 格式
            // 根据地址创建URL对象(网络访问的url)
            URL url = new URL(mUrl);
            // url.openConnection()打开网络链接
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");// 设置请求的方式
            urlConnection.setReadTimeout(5000);// 设置超时的时间
            urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            // 获取响应的状态码 404 200 505 302
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                is = urlConnection.getInputStream();
                // 创建字节输出流对象
                os = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    os.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                os.close();
                // 返回字符串
                String result = new String(os.toByteArray());
                return result;
            } else {
                System.out.println("------------------链接失败-----------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                is.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void submitWeixingLogin(String access_token, String openid, String nickname, String headimgurl, String third) {
        if (NetworkUtil.isNetworkAvailable(WXEntryActivity.this)) {
            Map<String, String> maps = new HashMap<String, String>();
            maps.put("access_token", access_token);
            maps.put("openid", openid);
            maps.put("userfrom", third);
            maps.put("nickname", nickname);
            maps.put("head_img", headimgurl);
            HttpRequestClient.getInstance(WXEntryActivity.this).createBaseApi().post(""
                    , maps, new  BaseObserver<ResponseBody>(WXEntryActivity.this) {

                        @Override
                        protected void onHandleSuccess(ResponseBody responseBody) {

                        }
                    });
        } else {
            ToastUtil.showLong(WXEntryActivity.this, "网络已断开，请检查您的网络!");
        }
    }

    public void loginIn() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

}