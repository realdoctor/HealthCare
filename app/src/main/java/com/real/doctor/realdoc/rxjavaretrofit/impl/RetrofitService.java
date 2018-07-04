package com.real.doctor.realdoc.rxjavaretrofit.impl;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
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
import retrofit2.http.Url;

/**
 * @author zhujiabin
 * @package rxjavaretrofit.impl
 * @fileName ${Name}
 * @Date 2018-1-3 0003
 * @describe TODO
 * @email zhujiabindragon@163.com
 */

public interface RetrofitService {

   /* @GET
    Call<NotificationObject> getNotifications(@Url String url,
                                              @QueryMap Map<String, String> maps);
*/

    //get 请求接口
    @GET
    Observable<ResponseBody> executeGet(
            @Url String url,
            @QueryMap Map<String, String> maps
    );

    //post 请求接口
    @FormUrlEncoded
    @POST
    Observable<ResponseBody> executePost(
            @Url String url,
            @FieldMap Map<String, String> maps);

    //json 请求数据接口
    @POST
    @Headers({"Content-Type: application/json", "Accept: application/json"})
   //需要添加头
    Observable<ResponseBody> json(
            @Url String url,
            @Body RequestBody jsonStr);

    //上传图片数据接口
    @Multipart
    @POST
    Observable<ResponseBody> upLoadFile(
            @Url String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @Multipart
    @POST
    Observable<ResponseBody> upLoadMap(
            @Url String url,
            @PartMap Map<String, RequestBody> params);

    //上传文件接口
    @POST
    Call<ResponseBody> uploadFiles(
            @Url String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    //下载接口，单个文件下载
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

}
