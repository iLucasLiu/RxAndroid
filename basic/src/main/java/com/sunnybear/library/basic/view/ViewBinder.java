package com.sunnybear.library.basic.view;

import android.content.Context;
import android.os.Bundle;

import com.sunnybear.library.basic.bus.RxSubscriptions;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.basic.presenter.PresenterFragment;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * 绑定View实例
 * Created by sunnybear on 16/1/29.
 */
public abstract class ViewBinder<P extends Presenter> implements View {
    protected Context mContext;
    protected P mPresenter;

    public ViewBinder(Presenter presenter) {
        mPresenter = (P) presenter;
        if (presenter instanceof PresenterActivity)
            mContext = (Context) presenter;
        else if (presenter instanceof PresenterFragment)
            mContext = ((PresenterFragment) presenter).getContext();
        else throw new ClassCastException("ViewBinder没有依赖PresenterActivity或者PresenterFragment");
    }

    @Override
    public void onBindView(Bundle args) {

    }

    @Override
    public void addListener() {

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
     * 获取Activity
     */
    private PresenterActivity getActivity() {
        if (mPresenter instanceof PresenterActivity)
            return (PresenterActivity) mPresenter;
        else if (mPresenter instanceof PresenterFragment)
            return (PresenterActivity) ((PresenterFragment) mPresenter).getActivity();
        else throw new ClassCastException("ViewBinder没有依赖PresenterActivity或者PresenterFragment");
    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void sendToPresenter(String tag, T model) {
        PresenterActivity activity = getActivity();
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
        PresenterActivity activity = getActivity();
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
        PresenterActivity activity = getActivity();
        Map<String, Flowable> mObservableMap = activity.getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> observable));
        activity.receiveObservableFromView(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receive(String tag) {
        PresenterActivity activity = getActivity();
        Flowable<T> observable = (Flowable<T>) activity.getObservables().remove(tag);
        if (observable != null)
            return observable.onBackpressureBuffer();
        return null;
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receiveArray(String tag) {
        PresenterActivity activity = getActivity();
        Flowable<T[]> observable = (Flowable<T[]>) activity.getObservables().remove(tag);
        if (observable != null)
            return observable.flatMap(ts -> Flowable.fromArray(ts).onBackpressureBuffer());
        return null;
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
