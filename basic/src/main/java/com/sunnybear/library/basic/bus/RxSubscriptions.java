package com.sunnybear.library.basic.bus;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 管理订阅者实例
 * Created by chenkai.gu on 2016/11/21.
 */
public final class RxSubscriptions {
    private static CompositeSubscription mSubscriptions = new CompositeSubscription();

    public static boolean isUnsubscribed() {
        return mSubscriptions.isUnsubscribed();
    }

    public static void add(Subscription subscription) {
        if (subscription != null)
            mSubscriptions.add(subscription);
    }

    public static void remove(Subscription subscription) {
        if (subscription != null)
            mSubscriptions.remove(subscription);
    }

    public static void clear() {
        mSubscriptions.clear();
    }

    public static void unsubscribe() {
        mSubscriptions.unsubscribe();
    }

    public static boolean hasSubscriptions() {
        return mSubscriptions.hasSubscriptions();
    }
}
