package com.sunnybear.rxandroid.view;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.DesignActivity;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class CoordinatorViewBinder extends ViewBinder<DesignActivity> {

    public CoordinatorViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_coordinator;
    }

    @Override
    public void onViewCreatedFinish() {

    }
}