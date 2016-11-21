package com.sunnybear.rxandroid.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.bus.RxBusSubscriber;
import com.sunnybear.library.basic.bus.RxEvent;
import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.rxandroid.model.RecyclerModelProcessor;
import com.sunnybear.rxandroid.view.RecyclerViewBinder;
import com.trello.rxlifecycle.android.ActivityEvent;

import rx.functions.Action1;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerActivity extends PresenterActivity<RecyclerViewBinder> {
    @InjectModel(RecyclerModelProcessor.class)
    private RecyclerModelProcessor mRecyclerModelProcessor;

    @Override
    protected RecyclerViewBinder getViewBinder(Context context) {
        return new RecyclerViewBinder(context);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        send("content", mRecyclerModelProcessor.getContent());
        RxEvent.subscriber(RxEvent.getEventSticky()
                .subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String tag, String s) {
                        switch (tag) {
                            case "sticky":
                                ToastUtils.showToastLong(mContext, s);
                                break;
                        }
                    }
                }));
    }

    @Override
    public void receiveObservableView(String tag) {
        switch (filterTag(tag)) {
            case "click":
                this.<Integer>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                ToastUtils.showToastLong(mContext, "点击了第" + integer + "项");
                            }
                        }).subscribe();
                break;
        }
    }
}
