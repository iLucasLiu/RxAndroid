package com.sunnybear.library.network.callback;

import java.io.Serializable;

/**
 * 通用的请求回调
 * Created by chenkai.gu on 2016/11/12.
 */
interface CommonCallback<T extends Serializable> {
    /**
     * 请求成功回调
     *
     * @param t 请求结果
     */
    void onSuccess(T t);

    /**
     * 请求失败回调
     *
     * @param statusCode 状态码
     * @param error      失败信息
     */
    void onFailure(int statusCode, String error);

    /**
     * 请求开始回调
     */
    void onStart();

    /**
     * 请求结束回调
     *
     * @param isSuccess 是否请求成功
     */
    void onFinish(boolean isSuccess);
}
