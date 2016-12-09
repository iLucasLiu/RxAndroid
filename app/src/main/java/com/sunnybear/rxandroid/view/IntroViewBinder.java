package com.sunnybear.rxandroid.view;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.IntroActivity;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class IntroViewBinder extends ViewBinder<IntroActivity> {
    // 控制ToolBar的变量
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;

    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    @Bind(R.id.iv_placeholder)
    ImageView mIvPlaceholder;// 大图片
    @Bind(R.id.fl_title)
    FrameLayout mFlTitle;// Title的FrameLayout
    @Bind(R.id.tv_toolbar_title)
    TextView mTvToolbarTitle;//标题栏Title
    @Bind(R.id.appbar)
    AppBarLayout mAppbar;//整个可以滑动的AppBar
    @Bind(R.id.tb_toolbar)
    Toolbar mTbToolbar;// 工具栏
    @Bind(R.id.ll_title_container)
    LinearLayout mLlTitleContainer;//Title的LinearLayout

    public IntroViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_intro;
    }

    @Override
    public void onViewCreatedFinish() {
        mTbToolbar.setTitle("");
        initParallaxValues();
    }

    @Override
    public void addListener() {
        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = Math.abs(verticalOffset) / maxScroll;
            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);
        });
    }

    /**
     * 控制Title的显示
     *
     * @param percentage
     */
    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mLlTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mLlTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    /**
     * 处理ToolBar的显示
     *
     * @param percentage
     */
    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage > PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTvToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }
        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTvToolbarTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    /**
     * 设置自动滑动的动画效果
     */
    private void initParallaxValues() {
        CollapsingToolbarLayout.LayoutParams petDetailsLp = (CollapsingToolbarLayout.LayoutParams) mIvPlaceholder.getLayoutParams();
        petDetailsLp.setParallaxMultiplier(0.9f);
        CollapsingToolbarLayout.LayoutParams petBackgroundLp = (CollapsingToolbarLayout.LayoutParams) mFlTitle.getLayoutParams();
        petBackgroundLp.setParallaxMultiplier(0.3f);
        mIvPlaceholder.setLayoutParams(petDetailsLp);
        mFlTitle.setLayoutParams(petBackgroundLp);
    }

    /**
     * 设置渐变的动画
     *
     * @param v
     * @param duration
     * @param visibility
     */
    private void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}