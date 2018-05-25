package com.real.doctor.realdoc.rxjavaretrofit.cache;

import android.util.Log;
import android.content.Context;

import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * BaseInterceptor
 */
public class BaseInterceptor implements Interceptor {
    private Map<String, String> headers;
    private Context context;

    public BaseInterceptor(Context context, Map<String, String> headers) {
        this.headers = headers;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {

            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {

                Log.e("headerKey", headerKey + "" + headers.get(headerKey));
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
        String token = (String) SPUtils.get(context, "token", "");
        if (EmptyUtils.isNotEmpty(token)) {
            builder.addHeader("Authorization", token);
        }
        return chain.proceed(builder.build());
    }
}