package com.sunnybear.rxandroid.model;

import android.util.Log;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.MathUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class DownloadModelProcessor extends ModelProcessor {

    public DownloadModelProcessor(PresenterActivity activity) {
        super(activity);
    }

    public void download(String url, String savePath) {
        RequestHelper.download(
                url, savePath, new DownloadCallback() {
                    @Override
                    public void onSuccess(File file) {
                        Logger.d(file.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(int statusCode, String error) {
                        Logger.e("statusCode:" + statusCode + "-----error:" + error);
                    }

                    @Override
                    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                        String percent = MathUtils.percent(bytesRead, contentLength, 2);
                        Log.e("RxAndroid", "percent=" + percent);
                    }
                }, mActivity.bindUntilEvent(ActivityEvent.STOP), false);
    }
}
