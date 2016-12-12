package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.sunnybear.library.util.Logger;

/**
 * Created by chenkai.gu on 2016/12/12.
 */
public class RotateBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    public RotateBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        float percentComplete = -(translationY / dependency.getHeight());
        Logger.i(-90 * percentComplete + "");
        child.setRotation(-90 * percentComplete);
        child.setTranslationY(translationY);
        return false;
    }
}
