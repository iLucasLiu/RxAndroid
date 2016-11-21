package com.sunnybear.library.basic.presenter;

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
}
