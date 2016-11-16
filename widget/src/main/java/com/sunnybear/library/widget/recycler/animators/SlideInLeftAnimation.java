package com.sunnybear.library.widget.recycler.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 左侧进入
 * Created by guchenkai on 2016/1/20.
 */
public class SlideInLeftAnimation implements IAnimation {
    @Override
    public Animator[] getAnimators(View itemView) {
        return new Animator[]{
                ObjectAnimator.ofFloat(itemView, "translationX", -itemView.getRootView().getWidth(), 0)
        };
    }
}
