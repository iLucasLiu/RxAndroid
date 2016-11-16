package com.sunnybear.library.basic.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.sunnybear.library.basic.Dispatch;
import com.sunnybear.library.basic.DispatchActivity;
import com.sunnybear.library.basic.R;
import com.sunnybear.library.eventbus.EventBusHelper;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.Map;

import rx.Observable;
import rx.functions.Func1;

/**
 * 绑定View实例
 * Created by sunnybear on 16/1/29.
 */
public abstract class ViewBinder<D extends Dispatch> implements ViewModelBridge {
    public static final String EVENT_SEND_OBSERVABLE = "send_observable";
    protected Context mContext;
    protected D mDispatch;
    protected Fragment[] mFragments;

    public ViewBinder(Context context) {
        this(context, null);
    }

    public ViewBinder(Context context, Fragment... fragments) {
        mContext = context;
        mDispatch = (D) context;
        if (!(mDispatch instanceof DispatchActivity))
            throw new RuntimeException("ViewBinder中的Content必须是DispatchActivity类型");
        if (fragments != null)
            mFragments = fragments;
        EventBusHelper.register(this);
    }

    @Override
    public void onBindView(Bundle args) {

    }

    @Override
    public void addListener() {
        addBackKey();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mDispatch = null;
        mContext = null;
        EventBusHelper.unregister(this);
    }

    @Override
    public void onRestart() {

    }

    /**
     * 添加返回键
     */
    private void addBackKey() {
        View view = ((DispatchActivity) mDispatch).findViewById(R.id.btn_back);
        if (view != null)
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DispatchActivity) mDispatch).onBackPressed();
                }
            });
    }

    /**
     * 将Model发送给Dispatch层
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void sendToDispatch(String tag, T model) {
        DispatchActivity activity = (DispatchActivity) mDispatch;
        Map<String, Observable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Observable.just(model));
        activity.receiveObservable(tag + TAG);
    }

    /**
     * 将Model发送给Dispatch层
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void sendToDispatch(String tag, T... models) {
        DispatchActivity activity = (DispatchActivity) mDispatch;
        Map<String, Observable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Observable.just(models));
        activity.receiveObservable(tag + TAG);
    }

    /**
     * 发送一个observable给Dispatch层
     *
     * @param tag    标签
     * @param observable 数据Model组
     * @param <T>    泛型
     */
    public final <T> void sendToView(String tag, Observable<T> observable) {
        DispatchActivity activity = (DispatchActivity) mDispatch;
        Map<String, Observable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, observable);
        activity.receiveObservable(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    public final <T> Observable<T> receive(String tag, ActivityEvent event) {
        DispatchActivity activity = (DispatchActivity) mDispatch;
        Observable<T> observable = (Observable<T>) activity.getObservables().remove(tag + activity.TAG);
        if (event != null)
            return observable.compose(activity.<T>bindUntilEvent(event));
        else
            return observable.compose(activity.<T>bindToLifecycle());
    }

    /**
     * 接收观察者并处理(全生命周期管理RxJava线程)
     *
     * @param tag 观察者标签
     */
    public final <T> Observable<T> receive(String tag) {
        return receive(tag, null);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    public final <T> Observable<T> receiveArray(String tag, ActivityEvent event) {
        DispatchActivity activity = (DispatchActivity) mDispatch;
        Observable<T[]> observable = (Observable<T[]>) activity.getObservables().remove(tag + activity.TAG);
        if (event != null)
            return observable.compose(activity.<T[]>bindUntilEvent(event))
                    .flatMap(new Func1<T[], Observable<T>>() {
                        @Override
                        public Observable<T> call(T[] ts) {
                            return Observable.from(ts);
                        }
                    });
        else
            return observable.compose(activity.<T[]>bindToLifecycle())
                    .flatMap(new Func1<T[], Observable<T>>() {
                        @Override
                        public Observable<T> call(T[] ts) {
                            return Observable.from(ts);
                        }
                    });
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Observable<T> receiveArray(String tag) {
        return receiveArray(tag, null);
    }

    /**
     * 接收观察者
     *
     * @param tag 观察者标签
     */
    public void receiveObservable(String tag) {

    }

    /**
     * 过滤标签
     *
     * @param tag 标签
     * @return 过滤后的标签
     */
    protected String filterTag(String tag) {
        return tag.split("\\-")[0];
    }
}
