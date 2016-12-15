package com.sunnybear.library.basic.presenter;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * Presenter标识
 * Created by sunnybear on 16/1/29.
 */
public interface Presenter {
    String TAG = "-dispatch";

    /**
     * 接收View层的的观察者并处理
     *
     * @param tag 观察者标签
     */
    void receiveObservableFromView(String tag);

    /**
     * 获取观察者管理器
     *
     * @return 观察者管理器
     */
    Map<String, Flowable> getObservables();
}
