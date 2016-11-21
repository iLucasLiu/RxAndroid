package com.sunnybear.library.basic.bus;

import java.io.Serializable;

import rx.Observable;
import rx.Subscription;

/**
 * 事件实体
 * Created by chenkai.gu on 2016/11/21.
 */
public class RxEvent<T> implements Serializable {
    public String tag;
    public T event;

    public RxEvent(String tag, T event) {
        this.tag = tag;
        this.event = event;
    }

    public static <T> void post(String tag, T event) {
        RxBus.getDefault().post(new RxEvent(tag, event));
    }

    public static <T> void postSticky(String tag, T event) {
        RxBus.getDefault().postSticky(new RxEvent(tag, event));
    }

    public static Observable<RxEvent> getEvent() {
        return RxBus.getDefault().toObservable(RxEvent.class);
    }

    public static Observable<RxEvent> getEventSticky() {
        return RxBus.getDefault().toObservableSticky(RxEvent.class);
    }

    public static void subscriber(Subscription subscription) {
        RxSubscriptions.add(subscription);
    }
}
