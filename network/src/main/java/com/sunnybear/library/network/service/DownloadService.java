package com.sunnybear.library.network.service;

import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.service.bind.BasicBinder;
import com.sunnybear.library.util.AppUtils;
import com.sunnybear.library.util.Logger;

import java.io.File;

/**
 * 下载文件使用用的Service
 * Created by chenkai.gu on 2016/10/20.
 */
public class DownloadService extends RxService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("DownloadService已绑定");
        return new BasicBinder<DownloadService>() {

            @Override
            public DownloadService getService() {
                return DownloadService.this;
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d("DownloadService绑定解除");
        AppUtils.stopRunningService(this, DownloadService.class);
        return super.onUnbind(intent);
    }

    /**
     * 下载文件,带有进度处理
     *
     * @param url      下载地址
     * @param filePath 保存文件的路径
     * @param callback 下载回调
     * @param isCover  是否覆盖
     */
    public void download(String url, String filePath, DownloadCallback callback, boolean isCover) {
        RequestHelper.download(url, filePath, callback, this.<File>bindToLifecycle(), isCover);
    }
}
