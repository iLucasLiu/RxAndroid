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
    public void receiveObservableFromView(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag, ActivityEvent.STOP)
                        .doOnNext(s -> Logger.d("Presenter接收到的字符串是:" + s))
                        .doOnComplete(() -> Logger.i("Presenter字符串接收完成"))
                        .subscribe();
                break;
            case "number":
                this.<Integer>receive(tag, ActivityEvent.STOP)
                        .filter(integer -> integer > 4)
                        .doOnNext(integer -> Log.d("RxAndroid", "Presenter接收到的数字是:" + integer.toString()))
                        .doOnComplete(() -> Logger.i("Presenter数字接收完成")).subscribe();
                break;
            case "request":
                this.<String[]>receive(tag, ActivityEvent.STOP)
                        .doOnNext(strings ->
                                mMainModelProcessor.getBaike(strings[0], strings[1], strings[2], strings[3], strings[4])
                        ).subscribe();
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
