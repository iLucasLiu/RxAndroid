package com.sunnybear.library.basic.view;

import android.os.Bundle;

/**
 * View和Model的桥梁,在DispatchActivity中使用
 * Created by sunnybear on 16/1/29.
 */
public interface ViewModelBridge {
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
}