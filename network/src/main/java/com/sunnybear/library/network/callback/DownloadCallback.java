package com.sunnybear.library.network.callback;

import com.sunnybear.library.network.progress.UIProgressResponseListener;
import com.sunnybear.library.util.Logger;

import java.io.File;

/**
 * 下载文件回调
 * Created by chenkai.gu on 2016/11/12.
 */
public abstract class DownloadCallback extends UIProgressResponseListener implements CommonCallback<File> {

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish(boolean isSuccess) {
        Logger.i(isSuccess ? "文件下载成功" : "文件下载失败");
    }
}
