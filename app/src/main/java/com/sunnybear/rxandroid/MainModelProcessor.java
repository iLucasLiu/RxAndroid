package com.sunnybear.rxandroid;

import android.content.Context;
import android.util.Log;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.MathUtils;

import java.io.File;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class MainModelProcessor extends ModelProcessor<MainActivity> {
    private RequestService mRequestService;

    public MainModelProcessor(Context context) {
        super(context);
        mRequestService = RetrofitProvider.create(RequestService.class);
    }

    public void getBaike(String scope, String format, String appid, String bk_key, String bk_length) {
        RequestHelper.request(
                mRequestService.getBaike(scope, format, appid, bk_key, bk_length),
                new RequestCallback<Baike>(mContext) {
                    @Override
                    public void onSuccess(Baike baike) {
                        mPresenter.send("result", Observable.just(baike)
                                .map(new Func1<Baike, String>() {
                                    @Override
                                    public String call(Baike baike) {
                                        return baike.toString();
                                    }
                                }));
                    }

                    @Override
                    public void onFailure(int statusCode, String error) {
                        Logger.e(error);
                    }
                }, mPresenter.bindToLifecycle());
    }

    public void download(String url, String savePath) {
        RequestHelper.download(
                url, savePath, new DownloadCallback() {
                    @Override
                    public void onSuccess(File file) {
                        Logger.d(file.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(int statusCode, String error) {
                        Logger.e("statusCode:" + statusCode + "-----error:" + error);
                    }

                    @Override
                    public void onUIResponseProgress(long bytesRead, long contentLength, boolean done) {
                        String percent = MathUtils.percent(bytesRead, contentLength, 2);
                        Log.e("RxAndroid", "percent=" + percent);
                    }
                }, mPresenter.<File>bindToLifecycle(), true);
    }
}
