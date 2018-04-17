package com.real.doctor.realdoc.rxjavaretrofit.down;

import android.content.Context;

import com.real.doctor.realdoc.rxjavaretrofit.manager.DownLoadManager;

import io.reactivex.subscribers.ResourceSubscriber;


/**
 * DownSubscriber
 *
 * @param <ResponseBody>
 */
public class DownSubscriber<ResponseBody> extends ResourceSubscriber<ResponseBody> {

    private DownCallBack callBack;
    private Context mContext;

    public DownSubscriber(DownCallBack callBack, Context context) {
        this.callBack = callBack;
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (callBack != null) {
            callBack.onError(e);
        }
    }

    @Override
    public void onComplete() {
        if (callBack != null) {
            callBack.onCompleted();
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        DownLoadManager.getInstance(callBack).writeResponseBodyToDisk(mContext, (okhttp3.ResponseBody) responseBody);
    }
}
