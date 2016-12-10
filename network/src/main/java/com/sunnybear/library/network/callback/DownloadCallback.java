package com.sunnybear.library.network.callback;

import com.sunnybear.library.network.file.DownloadInfo;
import com.sunnybear.library.network.progress.UIProgressResponseListener;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.PreferenceHelper;
import com.sunnybear.library.util.StringUtils;

import java.io.File;

/**
 * 下载文件回调
 * Created by chenkai.gu on 2016/11/12.
 */
public abstract class DownloadCallback extends UIProgressResponseListener implements CommonCallback<File> {
    private String url;
    private String filePath;

    public DownloadCallback() {
    }

    public DownloadCallback(String url, String filePath) {
        this.url = url;
        this.filePath = filePath;
    }

    @Override
    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
        if (!StringUtils.isEmpty(url)) {
            DownloadInfo downloadInfo = new DownloadInfo(url, filePath, bytesRead, contentLength);
            PreferenceHelper.save(url, downloadInfo);
        }
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
