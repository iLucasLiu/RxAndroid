package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.rxandroid.model.RecyclerModelProcessor;
import com.sunnybear.rxandroid.view.RecyclerViewBinder;
import com.trello.rxlifecycle2.android.ActivityEvent;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerActivity extends PresenterActivity<RecyclerViewBinder> {
    @InjectModel
    private RecyclerModelProcessor mRecyclerModelProcessor;

    @Override
    protected RecyclerViewBinder getViewBinder(Presenter presenter) {
        return new RecyclerViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        send("content", mRecyclerModelProcessor.getPositions());
    }

    @Override
    public void receiveObservableFromView(String tag) {
        switch (filterTag(tag)) {
            case "click":
                this.<Integer>receive(tag)
                        .doOnNext(integer -> {
                            ToastUtils.showToastLong(mContext, "点击了第" + integer + "项");
                            ImageUtils.addWatermark(SDCardUtils.getSDCardPath() + "/test.jpg"
                                    , "sunnybear", ImageUtils.WatermarkLocation.BOTTOM_RIGHT
                                    , RecyclerActivity.this.bindUntilEvent(ActivityEvent.STOP));
                        }).compose(bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
        }
    }
}
