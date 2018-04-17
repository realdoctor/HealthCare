
package com.real.doctor.realdoc.rxjavaretrofit.entity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.entity
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public abstract class BaseObserver<T> implements Observer<ResponseBody> {

    private static final String TAG = "BaseObserver";
    private Context mContext;

    protected BaseObserver(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(ResponseBody value) {
//        if (value.isSuccess()) {
//            T t = value.getData();
        onHandleSuccess((T) value);
//        } else {
//            onHandleError(value.getMsg());
//        }
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "error:" + e.toString());
    }

    @Override
    public void onComplete() {
        Log.d(TAG, "onComplete");
    }


    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

}
