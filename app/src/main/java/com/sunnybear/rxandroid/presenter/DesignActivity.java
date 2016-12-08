package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.rxandroid.view.CoordinatorViewBinder;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class DesignActivity extends PresenterActivity<CoordinatorViewBinder> {

    /*@Override
    protected DesignViewBinder getViewBinder(Presenter presenter) {
        return new DesignViewBinder(presenter);
    }*/

    @Override
    protected CoordinatorViewBinder getViewBinder(Presenter presenter) {
        return new CoordinatorViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
    }
}