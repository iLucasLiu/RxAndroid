package com.sunnybear.library.basic.bus;


import io.reactivex.functions.Consumer;

/**
 * RxBus订阅者
 * Created by chenkai.gu on 2016/11/21.
 */
public abstract class RxBusSubscriber<T> implements Consumer<RxEvent> {
    @Override
    public void accept(RxEvent event) throws Exception {
        onEvent(event.tag, (T) event.event);
    }

    protected abstract void onEvent(String tag, T t);
}
