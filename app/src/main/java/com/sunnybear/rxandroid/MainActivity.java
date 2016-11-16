package com.sunnybear.rxandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunnybear.library.basic.DispatchActivity;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.util.Logger;
import com.trello.rxlifecycle.android.ActivityEvent;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends DispatchActivity<MainViewBinder> {
    public RequestService mRequestService;

    @Override
    protected MainViewBinder getViewBinder(Context context) {
        return new MainViewBinder(context);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        mRequestService = RetrofitProvider.create(RequestService.class);

        sendToView("string", "Hello RxJava");
        sendToView("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Logger.d("Dispatch接收到的字符串是:" + s);
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("Dispatch字符串接收完成");
                            }
                        }).subscribe();
                break;
            case "number":
                this.<Integer>receiveArray(tag, ActivityEvent.STOP)
                        .filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                return integer > 4;
                            }
                        })
                        .doOnNext(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d("RxAndroid", "Dispatch接收到的数字是:" + integer.toString());
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("Dispatch数字接收完成");
                            }
                        }).subscribe();
                break;
            case "request":
                RequestHelper.request(
                        mRequestService.getBaike("103", "json", "379020", "西湖", "600"),
                        new RequestCallback<Baike>(mContext) {
                            @Override
                            public void onSuccess(Baike baike) {
                                sendToView("result", Observable.just(baike)
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
                        }, bindToLifecycle());
                break;
        }
    }
}
