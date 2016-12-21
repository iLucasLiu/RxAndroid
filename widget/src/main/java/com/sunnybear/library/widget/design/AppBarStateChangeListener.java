package com.sunnybear.library.widget.design;

import android.support.design.widget.AppBarLayout;

/**
 * 监听CollapsingToolbarLayout的展开与折叠
 * Created by chenkai.gu on 2016/12/20.
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    public enum State {
        /*扩展*/
        EXPANDED,
        /*收缩*/
        COLLAPSED,
        /*正常*/
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED)
                onStateChanged(appBarLayout, State.EXPANDED);
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED)
                onStateChanged(appBarLayout, State.COLLAPSED);
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE)
                onStateChanged(appBarLayout, State.IDLE);
            mCurrentState = State.IDLE;
        }
    }

    /**
     * 折叠状态回调
     *
     * @param appBarLayout appBarLayout
     * @param state        折叠状态
     */
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
