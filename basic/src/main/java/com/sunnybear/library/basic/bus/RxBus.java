package com.sunnybear.library.basic.bus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 响应式事件总线
 * Created by chenkai.gu on 2016/11/21.
 */
final class RxBus {
    private static volatile RxBus mDefaultInstance;
    private final Subject<Object, Object> mBus;
    /*粘滞事件*/
    private final Map<Class<?>, Object> mStickyEventMap;

    /*private List<Subscription> mSubscriptions;*/

    private RxBus() {
        /*PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者*/
        mBus = new SerializedSubject<>(PublishSubject.create());
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null)
            synchronized (RxBus.class) {
                if (mDefaultInstance == null)
                    mDefaultInstance = new RxBus();
            }
        return mDefaultInstance;
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    public void reset() {
        mDefaultInstance = null;
    }

    /**
     * 发送一个新的事件
     *
     * @param event 事件实例
     */
    public void post(Object event) {
        try {
            mBus.onNext(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送一个新Sticky事件
     *
     * @param event 事件实例
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * 根据传递的eventType类型返回特定类型(eventType)的被观察者
     *
     * @param type 事件类型
     * @param <T>  泛型
     * @return 事件被观察者
     */
    public <T> Observable<T> toObservable(Class<T> type) {
        return mBus.ofType(type);
    }

    /**
     * 根据传递的eventType类型返回特定类型(eventType)的被观察者
     *
     * @param type 事件类型
     * @param <T>  泛型
     * @return 事件被观察者
     */
    public <T> Observable<T> toObservableSticky(final Class<T> type) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = mBus.ofType(type);
            final Object event = mStickyEventMap.get(type);
            if (event != null)
                return observable.mergeWith(observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        subscriber.onNext(type.cast(event));
                    }
                }));
            else
                return observable;
        }
    }

    /**
     * 根据eventType获取Sticky事件
     *
     * @param type 事件类型
     * @param <T>  泛型
     * @return Sticky事件实例
     */
    public <T> T getStickyEvent(Class<T> type) {
        synchronized (mStickyEventMap) {
            return type.cast(mStickyEventMap.get(type));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     *
     * @param type 事件类型
     * @param <T>  泛型
     * @return Sticky事件实例
     */
    public <T> T removeStickyEvent(Class<T> type) {
        synchronized (mStickyEventMap) {
            return type.cast(mStickyEventMap.remove(type));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }
}
