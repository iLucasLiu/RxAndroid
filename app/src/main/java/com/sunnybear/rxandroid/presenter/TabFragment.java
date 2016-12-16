package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterFragment;
import com.sunnybear.rxandroid.view.TabViewBinder;

/**
 * Created by chenkai.gu on 2016/12/16.
 */
public class TabFragment extends PresenterFragment<TabViewBinder> {

    @Override
    protected TabViewBinder getViewBinder(Presenter presenter) {
        return new TabViewBinder(presenter);
    }

    @Override
    protected void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
    }
}