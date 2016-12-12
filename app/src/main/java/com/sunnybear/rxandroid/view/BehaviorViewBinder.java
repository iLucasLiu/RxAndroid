package com.sunnybear.rxandroid.view;

import android.support.design.widget.CoordinatorLayout;
import android.view.MotionEvent;
import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.DesignActivity;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/12.
 */
public class BehaviorViewBinder extends ViewBinder<DesignActivity> {
    @Bind(R.id.first)
    View mFirst;
    @Bind(R.id.root)
    CoordinatorLayout mRoot;
    @Bind(R.id.second)
    View mSecond;

    private float startX, startY;

    public BehaviorViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_behavior;
    }

    @Override
    public void onViewCreatedFinish() {
//        mFirst.setOnClickListener(view -> mFirst.setTranslationY(300));
        mFirst.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.setTranslationX(motionEvent.getRawX() - startX);
                    view.setTranslationY(motionEvent.getRawY() - startY);
                    break;
            }
            return true;
        });
        mSecond.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = motionEvent.getX();
                    startY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.setTranslationX(motionEvent.getRawX() - startX);
                    view.setTranslationY(motionEvent.getRawY() - startY);
                    break;
            }
            return true;
        });
    }
}