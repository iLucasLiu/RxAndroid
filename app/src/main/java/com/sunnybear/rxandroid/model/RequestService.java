package com.sunnybear.rxandroid.model;

import com.sunnybear.rxandroid.model.entity.Baike;
import com.sunnybear.rxandroid.model.entity.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public interface RequestService {

    @FormUrlEncoded
    @POST("BaikeLemmaCardApi")
    Call<Baike> getBaike(@Field("scope") String scope,
                         @Field("format") String format,
                         @Field("appid") String appid,
                         @Field("bk_key") String bk_key,
                         @Field("bk_length") String bk_length);

    @GET("login/login.htm")
    Call<Login> login(@Query("mobileLoginNumber") String mobileLoginNumber,
                      @Query("loginDeviceIMEI") String loginDeviceIMEI,
                      @Query("userLoginPasswd") String userLoginPasswd);
}
