package com.sunnybear.library.basic.view;

import android.content.Context;
import android.os.Bundle;

import com.sunnybear.library.basic.R;
import com.sunnybear.library.basic.bus.RxSubscriptions;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * 绑定View实例
 * Created by sunnybear on 16/1/29.
 */
public abstract class ViewBinder<P extends Presenter> implements View {
    protected Context mContext;
    protected P mPresenter;

    public ViewBinder(Context context) {
        mContext = context;
        if (!(context instanceof PresenterActivity))
            throw new RuntimeException("必须依赖PresenterActivity");
        mPresenter = (P) context;

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
        mPresenter = null;
        mContext = null;
        RxSubscriptions.clear();
    }

    @Override
    public void onRestart() {

    }

    /**
     * 添加返回键
     */
    private void addBackKey() {
        android.view.View view = ((PresenterActivity) mPresenter).findViewById(R.id.btn_back);
        if (view != null)
            view.setOnClickListener(v -> ((PresenterActivity) mPresenter).onBackPressed());
    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void sendToPresenter(String tag, T model) {
        PresenterActivity activity = (PresenterActivity) mPresenter;
        Map<String, Flowable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(model)
                    .onBackpressureBuffer()));
        activity.receiveObservableFromView(tag + TAG);
    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void sendToPresenter(String tag, T... models) {
        PresenterActivity activity = (PresenterActivity) mPresenter;
        Map<String, Flowable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(models)
                    .onBackpressureBuffer()));
        activity.receiveObservableFromView(tag + TAG);
    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void sendToPresenter(String tag, Flowable<T> observable) {
        PresenterActivity activity = (PresenterActivity) mPresenter;
        Map<String, Flowable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> observable));
        activity.receiveObservableFromView(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    public final <T> Flowable<T> receive(String tag, ActivityEvent event) {
        PresenterActivity activity = (PresenterActivity) mPresenter;
        Flowable<T> observable = (Flowable<T>) activity.getObservables().remove(tag);
        if (event != null)
            return observable.onBackpressureBuffer().compose(activity.bindUntilEvent(event));
        else
            return observable.onBackpressureBuffer().compose(activity.bindToLifecycle());
    }

    /**
     * 接收观察者并处理(全生命周期管理RxJava线程)
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receive(String tag) {
        return receive(tag, null);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    public final <T> Flowable<T> receiveArray(String tag, ActivityEvent event) {
        PresenterActivity activity = (PresenterActivity) mPresenter;
        Flowable<T[]> observable = (Flowable<T[]>) activity.getObservables().remove(tag);
        if (event != null)
            observable.compose(activity.bindUntilEvent(event));
        else
            observable.compose(activity.bindToLifecycle());
        return observable.flatMap(ts -> Flowable.fromArray(ts).onBackpressureBuffer());
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receiveArray(String tag) {
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
