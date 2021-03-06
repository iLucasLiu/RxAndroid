package com.sunnybear.library.network.callback;

import android.content.Context;

import com.sunnybear.library.loading.LoadingHUD;
import com.sunnybear.library.network.R;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ResourcesUtils;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 请求回调
 * Created by chenkai.gu on 2016/11/12.
 */
public abstract class RequestCallback<T extends Serializable> implements Callback<T>, CommonCallback<T> {
    protected Context mContext;
    private LoadingHUD mLoading;

    public RequestCallback() {
    }

    public RequestCallback(Context context) {
        mContext = context;
        mLoading = LoadingHUD.create(context)
                .setStyle(LoadingHUD.Style.SPIN_INDETERMINATE)
                .setLabel(ResourcesUtils.getString(context, R.string.loading))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5F);
    }

    @Override
    public final void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            processSession(getSession(response));
            onSuccess(response.body());
            onFinish(true);
        } else {
            onFailure(response.code(), response.message());
            onFinish(false);
        }
    }

    /**
     * 获得session
     *
     * @param response response
     * @return session
     */
    private String getSession(Response<T> response) {
        String session = "";
        Headers headers = response.headers();
        if (headers != null) {
            List<String> cookies = headers.values("Set-Cookie");
            if (cookies != null && cookies.size() > 0) {
                String cookie = cookies.get(0);
                session = cookie.substring(0, cookie.indexOf(";"));
            }
        }
        return session;
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

    /**
     * 请求开始回调
     */
    @Override
    public void onStart() {
        if (mLoading != null) mLoading.show();
    }

    /**
     * 请求结束回调
     *
     * @param isSuccess 是否请求成功
     */
    @Override
    public void onFinish(boolean isSuccess) {
        Logger.i(isSuccess ? "请求成功" : "请求失败");
        if (mLoading != null) mLoading.dismiss();
    }

    /**
     * 请求超时回调
     */
    @Override
    public void onTimeout() {
        Logger.e("请求超时");
        if (mLoading != null) mLoading.dismiss();
    }

    /**
     * 处理Session
     *
     * @param session session
     */
    @Override
    public void processSession(String session) {

    }
}
