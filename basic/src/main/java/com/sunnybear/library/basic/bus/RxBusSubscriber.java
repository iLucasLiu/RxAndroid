package com.sunnybear.library.basic.bus;


import com.sunnybear.library.util.Logger;

import rx.Subscriber;

/**
 * RxBus订阅者
 * Created by chenkai.gu on 2016/11/21.
 */
public abstract class RxBusSubscriber<T> extends Subscriber<RxEvent> {

    @Override
    public void onNext(RxEvent event) {
        onEvent(event.tag, (T) event.event);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        Logger.e(e.getMessage());
    }

    protected abstract void onEvent(String tag, T t);
}
