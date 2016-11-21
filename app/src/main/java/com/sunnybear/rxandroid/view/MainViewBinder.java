package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sunnybear.library.basic.bus.RxBusSubscriber;
import com.sunnybear.library.basic.bus.RxEvent;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.MainActivity;
import com.trello.rxlifecycle.android.ActivityEvent;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class MainViewBinder extends ViewBinder<MainActivity> implements View.OnClickListener {
    @Bind(R.id.tv_content)
    TextView mTvContent;

    public MainViewBinder(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreatedFinish() {
        RxEvent.subscriber(RxEvent.getEvent()
                .subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String tag, String s) {
                         switch (tag) {
                            case "RxBus":
                                mTvContent.setText(s);
                                break;
                        }
                    }
                }));
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Logger.d("View接收到的字符串是:" + s);
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("View字符串接收完成");
                            }
                        }).subscribe();
                break;
            case "number":
                this.<Integer>receiveArray(tag)
                        .filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                return integer > 4;
                            }
                        })
                        .doOnNext(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d("RxAndroid", "View接收到的数字是:" + integer.toString());
                            }
                        })
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                Logger.i("View数字接收完成");
                            }
                        }).subscribe();
                break;
            case "result":
                this.<String>receive(tag)
                        .doOnNext(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mTvContent.setText(s);
                            }
                        }).subscribe();
                break;
        }
    }

    @OnClick({R.id.btn_request, R.id.btn_download, R.id.btn_send, R.id.btn_start})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request:
                sendToPresenter("request", "103", "json", "379020", "西湖", "600");
                break;
            case R.id.btn_download:
                sendToPresenter("download", Observable.empty());
                break;
            case R.id.btn_send:
                sendToPresenter("string", "Hello RxJava");
                sendToPresenter("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
                break;
            case R.id.btn_start:
                sendToPresenter("start", Observable.empty());
                RxEvent.postSticky("sticky","粘滞事件");
                break;
        }
    }
}
