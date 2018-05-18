package com.real.doctor.realdoc.rxjavaretrofit.cache;

import android.util.Log;

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

    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {

            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {

                Log.e("headerKey", headerKey);
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
       builder.addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzc3Nzg1MDAzNiIsImlhdCI6MTUyNjM3Nzc1MCwic3ViIjoie1wibW9iaWxlUGhvbmVcIjpcIjEzNzc3ODUwMDM2XCIsXCJyZWZyZXNoVG9rZW5cIjpmYWxzZSxcInVzZXJJZFwiOjd9IiwiaXNzIjoiT25saW5lIEpXVCBCdWlsZGVyIiwiYXVkIjoia2FuZ2xpYW4iLCJleHAiOjE1MjY5ODI1NTAsIm5iZiI6MTUyNjM3Nzc1MH0.Ldhx4u-9OGH-2iWua-t403ZpMNsXUdaytVEBMPL2IpQ");
        builder.addHeader("userId", "7");
        return chain.proceed(builder.build());

    }
}