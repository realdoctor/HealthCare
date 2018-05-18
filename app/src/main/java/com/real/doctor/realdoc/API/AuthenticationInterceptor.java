package com.real.doctor.realdoc.API;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthenticationInterceptor implements Interceptor {

    private String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", authToken);

        Request request = builder.build();

        return chain.proceed(request);
    }


    /**
     *
     * Authorization
     * Request.Builder builder = chain.request()
     .newBuilder();
     if (headers != null && headers.size() > 0) {

     Set<String> keys = headers.keySet();
     for (String headerKey : keys) {

     builder.addHeader(headerKey, headers.get(headerKey)).build();
     }
     }
     builder.addHeader("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzc3Nzg1MDAzNiIsImlhdCI6MTUyNjM3Nzc1MCwic3ViIjoie1wibW9iaWxlUGhvbmVcIjpcIjEzNzc3ODUwMDM2XCIsXCJyZWZyZXNoVG9rZW5cIjpmYWxzZSxcInVzZXJJZFwiOjd9IiwiaXNzIjoiT25saW5lIEpXVCBCdWlsZGVyIiwiYXVkIjoia2FuZ2xpYW4iLCJleHAiOjE1MjY5ODI1NTAsIm5iZiI6MTUyNjM3Nzc1MH0.Ldhx4u-9OGH-2iWua-t403ZpMNsXUdaytVEBMPL2IpQ");
     return chain.proceed(builder.build());
     */
}
