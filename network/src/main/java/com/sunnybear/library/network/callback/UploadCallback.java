package com.sunnybear.library.network.callback;

import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.progress.UIProgressRequestListener;
import com.sunnybear.library.util.Logger;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 上传文件回调
 * Created by chenkai.gu on 2016/12/10.
 */
public abstract class UploadCallback<T extends Serializable> extends UIProgressRequestListener implements Callback<T>, CommonCallback<T> {

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
            onFinish(true);
        } else {
            onFailure(response.code(), response.message());
            onFinish(false);
        }
    }

    @Override
    public final void onFailure(Call<T> call, Throwable t) {
        if (t instanceof UnknownHostException || t instanceof SocketTimeoutException) {
            onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, "当前网络不可用");
            onTimeout();
        } else {
            onFailure(RetrofitProvider.StatusCode.STATUS_CODE_500, t.getMessage());
            onFinish(false);
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onFinish(boolean isSuccess) {
        Logger.i(isSuccess ? "文件上传成功" : "文件上传失败");
    }

    @Override
    public void onTimeout() {

    }
}
