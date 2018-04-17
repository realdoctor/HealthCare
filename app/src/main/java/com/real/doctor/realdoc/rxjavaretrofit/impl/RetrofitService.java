package com.real.doctor.realdoc.rxjavaretrofit.impl;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.impl
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public interface RetrofitService {

    //get 请求接口
    @Headers({
            "Allow:GET, HEAD, OPTIONS",
            "Content-Type:application/json",
            "Transfer-Encoding:chunked",
            "Vary:Cookie",
            "Date:Sun, 21 Jan 2018 15:48:28 GMT",
            "X-Application-Context:application:prod",
            "X-Frame-Options:SAMEORIGIN"
    })
    @GET("{url}")
    Observable<ResponseBody> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps
    );

    //post 请求接口
    @FormUrlEncoded
    @POST("{url}")
    Observable<ResponseBody> executePost(
            @Path("url") String url,
            @FieldMap Map<String, String> maps);

    //json 请求数据接口
    @POST("{url}")
    Observable<ResponseBody> json(
            @Path("url") String url,
            @Body RequestBody jsonStr);

    //上传图片数据接口
    @Multipart
    @POST("{url}")
    Observable<ResponseBody> upLoadFile(
            @Path("url") String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @Multipart
    @POST("{url}")
    Observable<ResponseBody> upLoadMap(
            @Path("url") String url,
            @PartMap Map<String, RequestBody> params);

    //上传文件接口
    @POST("{url}")
    Call<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    //下载接口，单个文件下载
    @Streaming
    @POST("{url}")
    Observable<ResponseBody> downloadFile(@Path("url") String fileUrl);
}
