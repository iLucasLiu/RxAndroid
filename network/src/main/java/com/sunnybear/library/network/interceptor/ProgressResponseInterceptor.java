package com.sunnybear.library.network.interceptor;

import com.sunnybear.library.network.progress.ProgressResponseBody;
import com.sunnybear.library.network.progress.UIProgressResponseListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 进度拦截器
 * Created by guchenkai on 2015/10/26.
 */
public class ProgressResponseInterceptor implements Interceptor {
    private UIProgressResponseListener mUIProgressResponseListener;

    public ProgressResponseInterceptor(UIProgressResponseListener progressResponseListener) {
        mUIProgressResponseListener = progressResponseListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), mUIProgressResponseListener)).build();
    }
}
