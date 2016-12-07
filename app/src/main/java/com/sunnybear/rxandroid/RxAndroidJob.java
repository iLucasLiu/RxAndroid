package com.sunnybear.rxandroid;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.sunnybear.library.util.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/12/6.
 */
public class RxAndroidJob extends Job {
    private String mJobText;

    public RxAndroidJob(int priority, String jobText) {
        super(new Params(priority).setRequiresNetwork(false).persist());
        Logger.e(new Params(priority).isNetworkRequired() + "");
        mJobText = jobText;
    }

    @Override
    public void onAdded() {
//        Logger.e("onAdded:" + Thread.currentThread().getName());
        Logger.i(mJobText + " onAdded");
    }

    @Override
    public void onRun() throws Throwable {
//        Logger.e("onRun:" + Thread.currentThread().getName());
        Flowable.timer(10, TimeUnit.SECONDS)
                .doOnComplete(() -> {
                    Logger.i(mJobText + " onRun");
                    Logger.i("onRun Finish");
                })
                .subscribe();
//        throw new NullPointerException();
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
//        Logger.e("onCancel:" + Thread.currentThread().getName());
        Logger.i(mJobText + " onCancel");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
//        Logger.e("shouldReRunOnThrowable:" + Thread.currentThread().getName());
        Logger.i("已重试" + runCount + "次");
        if (runCount == 3)//重试三次
            return RetryConstraint.CANCEL;
        //5秒重试一次
        RetryConstraint constraint = new RetryConstraint(true);
        constraint.setNewDelayInMs((long) 5000);
        return constraint;
    }
}
