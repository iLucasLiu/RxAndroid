package com.sunnybear.library.network;

import android.util.Log;

import com.sunnybear.library.network.interceptor.NetworkInterceptor;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

/**
 * Retrofit提供器
 * Created by chenkai.gu on 2016/11/12.
 */
public final class RetrofitProvider {
    private static RetrofitProvider INSTANCE;
    private static Retrofit mRetrofit;

    public RetrofitProvider(final String logTag, String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(OkHttpManager.getInstance()
                        .addNetworkInterceptor(new NetworkInterceptor())
                        .addNetworkInterceptor(new HttpLoggingInterceptor(
                                new HttpLoggingInterceptor.Logger() {
                                    @Override
                                    public void log(String message) {
                                        Log.i(logTag, message);
                                    }
                                }).setLevel(HttpLoggingInterceptor.Level.HEADERS)).build())
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    /**
     * RetrofitProvider单例初始化
     *
     * @param baseUrl 基础Url
     */
    public static RetrofitProvider initialize(String logTag, String baseUrl) {
        if (INSTANCE == null)
            synchronized (RetrofitProvider.class) {
                if (INSTANCE == null)
                    INSTANCE = new RetrofitProvider(logTag, baseUrl);
            }
        return INSTANCE;
    }

    /**
     * 获取Retrofit实例
     *
     * @return Retrofit实例
     */
    public static Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 创建service实例
     *
     * @param service service接口的class
     * @param <I>     泛型
     * @return service实例
     */
    public static <I> I create(Class<I> service) {
        return mRetrofit.create(service);
    }

    /**
     * HTTP状态码常量
     */
    public static class StatusCode {
        public static final int STATUS_CODE_205 = 205;
        public static final int STATUS_CODE_404 = 404;
        public static final int STATUS_CODE_500 = 500;
    }
}
