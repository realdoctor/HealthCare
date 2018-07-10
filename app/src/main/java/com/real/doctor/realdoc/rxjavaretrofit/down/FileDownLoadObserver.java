package com.real.doctor.realdoc.rxjavaretrofit.down;

import android.content.Context;

import com.real.doctor.realdoc.rxjavaretrofit.manager.DownLoadManager;

import io.reactivex.observers.DefaultObserver;
import okhttp3.ResponseBody;

/**
 * DownSubscriber
 *
 * @param <T>
 */
public class FileDownLoadObserver<T> extends DefaultObserver<T> {

    private DownCallBack callBack;
    private Context mContext;
    private String dirPath;

    public FileDownLoadObserver(DownCallBack callBack, String path, Context context) {
        this.callBack = callBack;
        mContext = context;
        dirPath = path;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    @Override
    public void onNext(T t) {
        DownLoadManager.getInstance(callBack).writeResponseBodyToDisk(mContext, (ResponseBody) t, dirPath);
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

}