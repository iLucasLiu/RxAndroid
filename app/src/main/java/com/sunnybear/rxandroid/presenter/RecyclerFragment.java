package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.BindModel;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterFragment;
import com.sunnybear.rxandroid.model.RecyclerModelProcessor;
import com.sunnybear.rxandroid.view.RecyclerViewBinder;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class RecyclerFragment extends PresenterFragment<RecyclerViewBinder> {
    @BindModel
    RecyclerModelProcessor mRecyclerModelProcessor;

    @Override
    protected RecyclerViewBinder getViewBinder(Presenter presenter) {
        return new RecyclerViewBinder(presenter);
    }

    @Override
    protected void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        sendToView("content", mRecyclerModelProcessor.getPositions());
    }
}
