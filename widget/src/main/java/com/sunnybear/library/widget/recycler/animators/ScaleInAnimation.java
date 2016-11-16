package com.sunnybear.library.widget.recycler.animators;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 缩放进入动画
 * Created by guchenkai on 2016/1/20.
 */
public class ScaleInAnimation implements IAnimation {
    private final float DEFAULT_SCALE_FROM = 0.5F;
    private final float mFrom;

    public ScaleInAnimation() {
        mFrom = DEFAULT_SCALE_FROM;
    }

    public ScaleInAnimation(float from) {
        mFrom = from;
    }

    @Override
    public Animator[] getAnimators(View itemView) {
        return new Animator[]{
                ObjectAnimator.ofFloat(itemView, "scaleX", mFrom, 1.0F),
                ObjectAnimator.ofFloat(itemView, "scaleY", mFrom, 1.0F)
        };
    }
}
