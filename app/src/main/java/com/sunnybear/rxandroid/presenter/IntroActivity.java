package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.rxandroid.view.IntroViewBinder;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class IntroActivity extends PresenterActivity<IntroViewBinder> {

    @Override
    protected IntroViewBinder getViewBinder(Presenter presenter) {
        return new IntroViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
    }
}