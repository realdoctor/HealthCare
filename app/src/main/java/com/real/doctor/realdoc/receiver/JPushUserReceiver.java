package com.real.doctor.realdoc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonObject;
import com.real.doctor.realdoc.activity.CaseControlActivity;
import com.real.doctor.realdoc.activity.ChatActivity;
import com.real.doctor.realdoc.activity.MyRevisitActivity;
import com.real.doctor.realdoc.fragment.HomeFragment;
import com.real.doctor.realdoc.greendao.table.PushInfoManager;
import com.real.doctor.realdoc.greendao.table.SaveDocManager;
import com.real.doctor.realdoc.model.PushInfoBean;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DateUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushUserReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushUserReceiver";
    private String extra;
    private String time;
    private static String tagId;
    private static String userId;
    private static String mobile;
    private static String imageUrl;
    private static String userName;
    private String myUserId;
    private String title;
    private PushInfoManager instance;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                myUserId = (String) SPUtils.get(context, Constants.USER_KEY, "");
                extra = (String) bundle.get(JPushInterface.EXTRA_EXTRA);
                time = DateUtil.timeStamp2Date(DateUtil.timeStamp(), "MM月dd日 HH:mm");
                //发送广播更新首页
                String info = (String) bundle.get(JPushInterface.EXTRA_ALERT);
                title = (String) bundle.get(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                JSONObject object = new JSONObject(extra);
                if (DocUtils.hasValue(object, "tagId")) {
                    tagId = object.getString("tagId");
                }
                if (DocUtils.hasValue(object, "userId")) {
                    userId = object.getString("userId");
                }
                if (DocUtils.hasValue(object, "mobilePhone")) {
                    mobile = object.getString("mobilePhone");
                }
                if (DocUtils.hasValue(object, "imageUrl")) {
                    imageUrl = object.getString("imageUrl");
                }
                if (DocUtils.hasValue(object, "userName")) {
                    userName = object.getString("userName");
                }
                //聊天所需的头像,名称
                if (EmptyUtils.isNotEmpty(userName)) {
                    SPUtils.put(context, "fromRealName", userName);
                }
                if (EmptyUtils.isNotEmpty(imageUrl)) {
                    SPUtils.put(context, "fromImageUrl", imageUrl);
                } else {
                    SPUtils.put(context, "fromImageUrl", "");
                }
                processTagId(context, info, tagId);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                if (tagId.equals("0")) {
                    //病人,当病人接收到医生的回复后,跳转到我的复诊界面看答案
                    Intent i = new Intent(context, MyRevisitActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else if (tagId.equals("1")) {
                    //医生,当病人上传了病历文件后,通知医生到患者管理界面
                    Intent i = new Intent(context, CaseControlActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                } else if (tagId.equals("2")) {
                    //跳转到聊天界面
                    Intent i = new Intent(context, ChatActivity.class);
                    //i.putExtra("str", str);
                    //此处必须这么填,为了参数对应
                    i.putExtra("userId", mobile);
                    i.putExtra("doctorUserId", userId);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);
                }
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            System.out.print(e);
        }

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //send msg to HomeFragment
    private void processTagId(Context context, String info, String tagId) {
        //保存信息到数据库
        PushInfoBean bean = new PushInfoBean();
        bean.setUserId(myUserId);
        bean.setContent(info);
        bean.setTitle(title);
        bean.setTabId(tagId);
        bean.setFromUserId(userId);
        bean.setTime(time);
        bean.setFromMobile(mobile);
        instance = PushInfoManager.getInstance(context);
        instance.insertPushInfo(context, bean);
        Intent msgIntent = new Intent(HomeFragment.SHOW_BOAST_INFO);
        msgIntent.putExtra("info", info);
        msgIntent.putExtra("tagId", tagId);
        msgIntent.putExtra("userId", userId);
        msgIntent.putExtra("fromMobile", mobile);
        msgIntent.putExtra("time", time);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
    }
}
