package com.sunnybear.library.network.file;

import java.io.Serializable;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 文件操作服务
 * Created by chenkai.gu on 2016/11/12.
 */
public interface FileOperationService {

    /*下载文件*/
    @GET
    @Streaming
    Flowable<ResponseBody> download(@Url String url);

    /*上传文件*/
    @POST
    <T extends Serializable> Call<T> upload(@Url String url, @Body RequestBody body);
}
