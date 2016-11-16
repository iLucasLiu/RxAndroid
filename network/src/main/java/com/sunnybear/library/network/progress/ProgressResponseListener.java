package com.sunnybear.library.network.progress;

/**
 * 响应体进度回调接口，比如用于文件下载中
 * Created by guchenkai on 2015/10/26.
 */
interface ProgressResponseListener {

    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
