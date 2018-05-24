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

                Log.e("headerKey", headerKey+""+headers.get(headerKey));
                builder.addHeader(headerKey, headers.get(headerKey)).build();
            }
        }
      builder.addHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNTk2NTEyOTEzOCIsImlhdCI6MTUyNzA0NDA3NSwic3ViIjoie1wibW9iaWxlUGhvbmVcIjpcIjE1OTY1MTI5MTM4XCIsXCJyZWFsTmFtZVwiOlwiODg4OFwiLFwidXNlcklkXCI6NX0iLCJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJhdWQiOiJrYW5nbGlhbiIsImV4cCI6MTUyNzY0ODg3NSwibmJmIjoxNTI3MDQ0MDc1fQ.-OCAE-3SaDjgJ68DYaCnOcErYaZzJVtT-SFC-mpnPFc");
//        builder.addHeader("userId", "7");
        return chain.proceed(builder.build());

    }
}