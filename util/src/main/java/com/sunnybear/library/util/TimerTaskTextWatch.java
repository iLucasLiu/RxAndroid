package com.sunnybear.library.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.trello.rxlifecycle2.LifecycleTransformer;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 输入框输入定时任务触发监听(定时时间为秒值)
 * Created by chenkai.gu on 2016/11/30.
 */
public abstract class TimerTaskTextWatch implements TextWatcher {
    private Disposable mDisposable;
    private long mLastChangeTime;
    private long mDelayTime;
    private LifecycleTransformer<Long> mTransformer;

    public TimerTaskTextWatch(long delayTime, LifecycleTransformer<Long> transformer) {
        mDelayTime = delayTime;
        mTransformer = transformer;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mLastChangeTime != 0
                && (System.currentTimeMillis() - mLastChangeTime) < (mDelayTime * 1000)
                && !mDisposable.isDisposed())
            mDisposable.dispose();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!StringUtils.isEmpty(s.toString()))
            mDisposable = Flowable.timer(mDelayTime, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                    .compose(mTransformer)
                    .subscribe(aLong -> onTriggerTask(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
        mLastChangeTime = System.currentTimeMillis();
    }

    /**
     * 定时触发任务
     *
     * @param input 输入框中当前输入值
     */
    protected abstract void onTriggerTask(String input);
}
