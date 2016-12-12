package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenkai.gu on 2016/12/12.
 */
public class FollowBehavior extends CoordinatorLayout.Behavior<View> {
    public FollowBehavior() {
    }

    public FollowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof View;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setX(dependency.getX());
        child.setY(dependency.getY() + dependency.getHeight());
        return true;
    }
}
