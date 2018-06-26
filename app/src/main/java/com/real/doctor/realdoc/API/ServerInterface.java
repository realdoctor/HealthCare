package com.real.doctor.realdoc.API;

import com.real.doctor.realdoc.API.NotificationsPOJO.NotificationObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerInterface {


    @GET("/user/message/noticeList")
    Call<NotificationObject> getNotifications(@Header("Authorization") String token, @Query("pageNum") int pageNum, @Query("pageSize") int pageSize, @Query("userId") int id);

}