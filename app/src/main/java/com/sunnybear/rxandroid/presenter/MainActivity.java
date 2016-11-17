package com.sunnybear.rxandroid.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.rxandroid.view.MainViewBinder;
import com.sunnybear.rxandroid.model.DownloadModelProcessor;
import com.sunnybear.rxandroid.model.MainModelProcessor;
import com.trello.rxlifecycle.android.ActivityEvent;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

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
    }

    @Override
    public void receiveObservableView(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Logger.d("Presenter接收到的字符串是:" + s);
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("Presenter字符串接收完成");
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
                                Log.d("RxAndroid", "Presenter接收到的数字是:" + integer.toString());
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("Presenter数字接收完成");
                            }
                        }).subscribe();
                break;
            case "request":
                this.<String[]>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<String[]>() {
                            @Override
                            public void call(String[] strings) {
                                mMainModelProcessor.getBaike(strings[0], strings[1], strings[2], strings[3], strings[4]);
                            }
                        }).subscribe();
                break;
            case "download":
                mDownloadModelProcessor.download(
                        "http://10.103.18.196:8089/SFAInterface/appservice/downloadFile.htm?mobileLoginNumber=100",
                        SDCardUtils.getSDCardPath() + "/rxjava/100.zip");
                break;
        }
    }
}
