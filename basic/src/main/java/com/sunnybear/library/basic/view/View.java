package com.sunnybear.library.basic.view;

import android.os.Bundle;

import java.util.Map;

import io.reactivex.Flowable;

/**
 * View标示,做View层表现处理只能传递给Presenter
 * Created by sunnybear on 16/1/29.
 */
public interface View {
    String TAG = "-view";

    /**
     * 设置布局id
     *
     * @return 布局id
     */
    int getLayoutId();

    /**
     * 初始化控件数据
     *
     * @param args 传递参数
     */
    void onBindView(Bundle args);

    /**
     * 布局初始化完成回调
     */
    void onViewCreatedFinish();

    /**
     * 添加监听器
     */
    void addListener();

    /**
     * 开始
     */
    void onStart();

    /**
     * 交互
     */
    void onResume();

    /**
     * 暂停
     */
    void onPause();

    /**
     * 停止
     */
    void onStop();

    /**
     * 销毁
     */
    void onDestroy();

    /**
     * 重新开始
     */
    void onRestart();

    /**
     * 接收观察者
     *
     * @param tag 观察者标签
     */
    void receiveObservableFromPresenter(String tag);

    /**
     * 获取观察者管理器
     *
     * @return 观察者管理器
     */
    Map<String, Flowable> getObservables();
}