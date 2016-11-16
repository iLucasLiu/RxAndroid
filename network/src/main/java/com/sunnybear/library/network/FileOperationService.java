package com.sunnybear.library.network;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 文件操作服务
 * Created by chenkai.gu on 2016/11/12.
 */
public interface FileOperationService {

    /*下载大文件*/
    @GET
    @Streaming
    Observable<ResponseBody> download(@Url String fileUrl);
}
