package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.BindModel;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.rxandroid.model.RecyclerModelProcessor;
import com.sunnybear.rxandroid.view.RecyclerHeaderViewBinder;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class RecyclerHeaderActivity extends PresenterActivity<RecyclerHeaderViewBinder> {
    @BindModel
    RecyclerModelProcessor mRecyclerModelProcessor;

    @Override
    protected RecyclerHeaderViewBinder getViewBinder(Presenter presenter) {
        return new RecyclerHeaderViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        sendToView("content", mRecyclerModelProcessor.getPositions());
    }
}