package com.sunnybear.library.network.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;

import javax.annotation.Nonnull;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * RxJava使用Service生命周期管理
 * Created by chenkai.gu on 2016/11/16.
 */
public abstract class RxService extends Service implements LifecycleProvider<ServiceEvent> {

    private BehaviorSubject<ServiceEvent> lifecycleSubject = BehaviorSubject.create();

    @Nonnull
    @Override
    @CheckResult
    public Observable<ServiceEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Nonnull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ServiceEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Nonnull
    @Override
    @CheckResult
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return null;
    }

    @Override
    public void onCreate() {
        lifecycleSubject.onNext(ServiceEvent.CREATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lifecycleSubject.onNext(ServiceEvent.START_COMMAND);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.BIND);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        lifecycleSubject.onNext(ServiceEvent.UNBIND);
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ServiceEvent.DESTROY);
        super.onDestroy();
    }
}
