package com.sunnybear.library.network.callback;

import android.util.Log;

import com.sunnybear.library.network.progress.UIProgressResponseListener;
import com.sunnybear.library.util.Logger;

import java.io.File;

/**
 * 下载文件回调
 * Created by chenkai.gu on 2016/11/12.
 */
public abstract class DownloadCallback extends UIProgressResponseListener implements CommonCallback<File> {

    public DownloadCallback() {
    }

    @Override
    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
        Log.e("DownloadCallback", "bytesRead:" + bytesRead + "====contentLength:" + contentLength + "====done:" + done);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish(boolean isSuccess) {
        Logger.i(isSuccess ? "文件下载成功" : "文件下载失败");
    }

    @Override
    public void onTimeout() {

    }
}
