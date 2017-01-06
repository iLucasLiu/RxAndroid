package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.sunnybear.library.widget.recycler.QuickRecyclerView;

/**
 * Created by chenkai.gu on 2016/12/13.
 */
public class AlphaBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private float percentComplete;

    public AlphaBehavior() {
    }

    public AlphaBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof QuickRecyclerView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = ((QuickRecyclerView) dependency).computeVerticalScrollOffset();
        Log.i("AlphaBehavior", translationY + "");
        float percentComplete = Math.min(1, translationY / child.getHeight());
        Log.e("AlphaBehavior", percentComplete + "");
        child.setAlpha(percentComplete);
        return true;
    }
}
