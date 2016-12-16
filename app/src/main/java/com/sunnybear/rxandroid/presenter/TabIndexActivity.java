package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.rxandroid.view.TabIndexViewBinder;

/**
 * Created by chenkai.gu on 2016/12/16.
 */
public class TabIndexActivity extends PresenterActivity<TabIndexViewBinder> {

    @Override
    protected TabIndexViewBinder getViewBinder(Presenter presenter) {
        return new TabIndexViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
    }
}