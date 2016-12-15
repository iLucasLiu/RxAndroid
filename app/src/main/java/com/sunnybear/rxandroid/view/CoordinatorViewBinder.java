package com.sunnybear.rxandroid.view;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutCompat;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.DesignActivity;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class CoordinatorViewBinder extends ViewBinder<DesignActivity> {
    @Bind(R.id.scroll)
    NestedScrollView mScroll;
    @Bind(R.id.ll_scroll)
    LinearLayoutCompat mLlScroll;

    public CoordinatorViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_coordinator;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        mLlScroll.setNestedScrollingEnabled(false);
    }
}