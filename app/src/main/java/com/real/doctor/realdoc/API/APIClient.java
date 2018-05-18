package com.real.doctor.realdoc.API;

import android.net.TrafficStats;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.real.doctor.realdoc.rxjavaretrofit.cache.BaseInterceptor;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Lowlite on 07/11/2017.
 */

public class APIClient {

    public static final String BASE_URL = "http://47.98.156.204:8088";
    int pageNum = 1;
    int pageSize = 10;
   // private static Retrofit retrofit = null;


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();


    public static <S> S createService(
            Class<S> serviceClass, Map<String, String> map) {
        if (!TextUtils.isEmpty(map.get("userId"))) {
           // String authToken = Credentials.basic(username, password);
            String authToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzc3Nzg1MDAzNiIsImlhdCI6MTUyNjM3Nzc1MCwic3ViIjoie1wibW9iaWxlUGhvbmVcIjpcIjEzNzc3ODUwMDM2XCIsXCJyZWZyZXNoVG9rZW5cIjpmYWxzZSxcInVzZXJJZFwiOjd9IiwiaXNzIjoiT25saW5lIEpXVCBCdWlsZGVyIiwiYXVkIjoia2FuZ2xpYW4iLCJleHAiOjE1MjY5ODI1NTAsIm5iZiI6MTUyNjM3Nzc1MH0.Ldhx4u-9OGH-2iWua-t403ZpMNsXUdaytVEBMPL2IpQ";
            return createService(serviceClass, map, authToken);
        }

        return createService(serviceClass, null);
    }

    public static <S> S createService(
            Class<S> serviceClass, Map<String, String> map, final String authToken) {
        /*if (!TextUtils.isEmpty(map.get("userId"))) {
            //AuthenticationInterceptor baseInterceptor =new AuthenticationInterceptor(authToken);
            BaseInterceptor baseInterceptor = new BaseInterceptor(map);

            if (!httpClient.interceptors().contains(baseInterceptor)) {
                httpClient.addInterceptor(baseInterceptor);


                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }*/

        return retrofit.create(serviceClass);
    }


}
