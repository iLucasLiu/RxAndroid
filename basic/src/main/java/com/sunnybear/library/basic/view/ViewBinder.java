package com.sunnybear.library.basic.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

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
    protected ViewBinder mViewBinder;

    public ViewBinder(Presenter presenter) {
        mPresenter = (P) presenter;
        mViewBinder = this;
        if (presenter instanceof PresenterActivity)
            mContext = (Context) presenter;
        else if (presenter instanceof PresenterFragment)
            mContext = ((PresenterFragment) presenter).getContext();
        else throw new ClassCastException("ViewBinder没有依赖PresenterActivity或者PresenterFragment");
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 设置Toolbar
     *
     * @param toolbar Toolbar
     */
    protected void setSupportActionBar(Toolbar toolbar) {
        if (mPresenter instanceof PresenterActivity)
            ((PresenterActivity) mPresenter).setSupportActionBar(toolbar);
    }

    /**
     * 获得Toolbar
     *
     * @return Toolbar
     */
    protected ActionBar getSupportActionBar() {
        if (mPresenter instanceof PresenterActivity)
            return ((PresenterActivity) mPresenter).getSupportActionBar();
        return null;
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
     * 获取观察者集合
     */
    @Override
    public final Map<String, Flowable> getObservables() {
        return mPresenter.getObservables();
    }

    /**
     * 接收观察者
     *
     * @param tag 观察者标签
     */
    @Override
    public void receiveObservableFromPresenter(String tag) {

    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void sendToPresenter(String tag, T model) {
        Map<String, Flowable> mObservableMap = getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(model)
                    .onBackpressureBuffer()));
        mPresenter.receiveObservableFromView(tag + TAG);
    }

    /**
     * 将Model发送给Presenter层
     *
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void sendToPresenter(String tag, T... models) {
        Map<String, Flowable> mObservableMap = getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(models)
                    .onBackpressureBuffer()));
        mPresenter.receiveObservableFromView(tag + TAG);
    }

    /**
     * 将Model发送给Presenter层
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void sendToPresenter(String tag, Flowable<T> observable) {
        Map<String, Flowable> mObservableMap = getObservables();
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> observable));
        mPresenter.receiveObservableFromView(tag + TAG);
    }

    /**
     * 发送一个动作给Presenter层
     *
     * @param tag 标签
     */
    public final void sendToPresenter(String tag) {
        mPresenter.receiveObservableFromView(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     * @param <T> 泛型
     */
    protected final <T> Flowable<T> receiver(String tag) {
        Flowable<T> observable = (Flowable<T>) getObservables().remove(tag);
        if (observable != null)
            return observable.onBackpressureBuffer();
        return null;
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     * @param <T> 泛型
     */
    protected final <T> Flowable<T> receiverArray(String tag) {
        Flowable<T[]> observable = (Flowable<T[]>) getObservables().remove(tag);
        if (observable != null)
            return observable.flatMap(ts -> Flowable.fromArray(ts).onBackpressureBuffer());
        return null;
    }

    /**
     * 接收观察者
     *
     * @param tag 观察者标签
     * @param <T> 泛型
     */
    protected final <T> Flowable<T> receiverFlowable(String tag) {
        return (Flowable<T>) getObservables().remove(tag);
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
