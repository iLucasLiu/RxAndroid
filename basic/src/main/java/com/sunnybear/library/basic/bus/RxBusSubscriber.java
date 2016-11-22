package com.sunnybear.library.basic.bus;


import com.sunnybear.library.util.Logger;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * RxBus订阅者
 * Created by chenkai.gu on 2016/11/21.
 */
public abstract class RxBusSubscriber<T> implements Subscriber<RxEvent> {

    @Override
    public void onNext(RxEvent event) {
        onEvent(event.tag, (T) event.event);
    }

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Integer.MAX_VALUE);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Logger.e(e.getMessage());
    }

    protected abstract void onEvent(String tag, T t);
}
