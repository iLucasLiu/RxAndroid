package com.sunnybear.library.basic.bus;

import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.ListCompositeDisposable;

/**
 * 管理订阅者实例
 * Created by chenkai.gu on 2016/11/21.
 */
public final class RxSubscriptions {
    private static ListCompositeDisposable mDisposables = new ListCompositeDisposable();

    public static boolean isDisposed() {
        return mDisposables.isDisposed();
    }

    public static void add(Disposable disposable) {
        if (disposable != null)
            mDisposables.add(disposable);
    }

    public static void remove(Disposable Disposable) {
        if (Disposable != null)
            mDisposables.remove(Disposable);
    }

    public static void clear() {
        mDisposables.clear();
    }

    public static void dispose() {
        mDisposables.dispose();
    }
}
