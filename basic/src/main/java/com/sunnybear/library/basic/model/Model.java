package com.sunnybear.library.basic.model;

/**
 * model标示,做Model层数据处理只能传递给Presenter
 * Created by chenkai.gu on 2016/11/17.
 */
public interface Model {
    String TAG = "-model";

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
