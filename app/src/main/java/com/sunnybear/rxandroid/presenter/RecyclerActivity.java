package com.sunnybear.rxandroid.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.rxandroid.model.RecyclerModelProcessor;
import com.sunnybear.rxandroid.view.RecyclerViewBinder;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Consumer;

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
    }

    @Override
    public void receiveObservableFromView(String tag) {
        switch (filterTag(tag)) {
            case "click":
                this.<Integer>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                ToastUtils.showToastLong(mContext, "点击了第" + integer + "项");
                                ImageUtils.addWatermark(SDCardUtils.getSDCardPath() + "/test.jpg"
                                        , "sunnybear", ImageUtils.WatermarkLocation.BOTTOM_RIGHT
                                        , RecyclerActivity.this.<String>bindUntilEvent(ActivityEvent.STOP));
                            }
                        }).subscribe();
                break;
        }
    }
}
