package com.sunnybear.rxandroid.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.rxandroid.model.DownloadModelProcessor;
import com.sunnybear.rxandroid.model.MainModelProcessor;
import com.sunnybear.rxandroid.view.MainViewBinder;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class MainActivity extends PresenterActivity<MainViewBinder> {
    @InjectModel(MainModelProcessor.class)
    private MainModelProcessor mMainModelProcessor;
    @InjectModel(DownloadModelProcessor.class)
    private DownloadModelProcessor mDownloadModelProcessor;

    @Override
    protected MainViewBinder getViewBinder(Context context) {
        return new MainViewBinder(context);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);

        send("string", "Hello RxJava");
        send("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);

        Flowable.empty().subscribe(new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void receiveObservableFromView(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                Logger.d("Presenter接收到的字符串是:" + s);
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                Logger.i("Presenter字符串接收完成");
                            }
                        }).subscribe();
                break;
            case "number":
                this.<Integer>receiveArray(tag, ActivityEvent.STOP)
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer > 4;
                            }
                        })
                        .doOnNext(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                Log.d("RxAndroid", "Presenter接收到的数字是:" + integer.toString());
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                Logger.i("Presenter数字接收完成");
                            }
                        }).subscribe();
                break;
            case "request":
                this.<String[]>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Consumer<String[]>() {
                            @Override
                            public void accept(String[] strings) throws Exception {
                                mMainModelProcessor.getBaike(strings[0], strings[1], strings[2], strings[3], strings[4]);
                            }
                        }).subscribe();
                break;
            case "download":
                mDownloadModelProcessor.download(
                        "http://10.103.18.196:8089/SFAInterface/appservice/downloadFile.htm?mobileLoginNumber=100",
                        SDCardUtils.getSDCardPath() + "/rxjava/100.zip");
                break;
            case "start":
                startActivity(new Intent(mContext, RecyclerActivity.class));
                break;
        }
    }
}
