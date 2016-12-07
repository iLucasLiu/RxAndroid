package com.sunnybear.rxandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.MathUtils;

import java.io.File;

import io.reactivex.disposables.Disposable;

/**
 * Created by chenkai.gu on 2016/12/7.
 */
public class DownloadJob extends Job {
    private String url;
    private String savePath;
    private Disposable mDisposable;

    public DownloadJob(String url, String savePath) {
        super(new Params(10000).requireNetwork().persist());
        this.url = url;
        this.savePath = savePath;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mDisposable = RequestHelper.download(
                url, savePath, new DownloadCallback() {
                    @Override
                    public void onSuccess(File file) {
                        Logger.e("onSuccess:" + Thread.currentThread().getName());
                        Logger.d(file.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(int statusCode, String error) {
                        Logger.e("onFailure:" + Thread.currentThread().getName());
                        Logger.e("statusCode:" + statusCode + "-----error:" + error);
                    }

                    @Override
                    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                        Log.e("RxAndroid", "onUIResponseProgress:" + Thread.currentThread().getName());
                        String percent = MathUtils.percent(bytesRead, contentLength, 2);
                        Log.e("RxAndroid", "percent=" + percent);
                    }
                }, false);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        if (mDisposable != null && !mDisposable.isDisposed()) mDisposable.dispose();
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        Logger.i("已重试" + runCount + "次");
        if (runCount == 3)//重试三次
            return RetryConstraint.CANCEL;
        //5秒重试一次
        RetryConstraint constraint = new RetryConstraint(true);
        constraint.setNewDelayInMs((long) 5000);
        return constraint;
    }
}
