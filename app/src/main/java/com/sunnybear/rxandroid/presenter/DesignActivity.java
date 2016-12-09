package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.view.CollapsingViewBinder;
import com.sunnybear.rxandroid.view.CoordinatorViewBinder;
import com.sunnybear.rxandroid.view.DesignViewBinder;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class DesignActivity extends PresenterActivity<ViewBinder> {
    public static final String BUNDLE_VIEW_BINDER_TYPE = "view_binder_type";
    private int viewBinderType = 0;

    @Override
    protected ViewBinder getViewBinder(Presenter presenter) {
        switch (viewBinderType) {
            case 1:
                return new DesignViewBinder(presenter);
            case 2:
                return new CoordinatorViewBinder(presenter);
            case 3:
                return new CollapsingViewBinder(presenter);
        }
        return null;
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
    }

    @Override
    protected void onBundle(Bundle args) {
        super.onBundle(args);
        viewBinderType = Integer.parseInt(args.getString(BUNDLE_VIEW_BINDER_TYPE));
    }
}