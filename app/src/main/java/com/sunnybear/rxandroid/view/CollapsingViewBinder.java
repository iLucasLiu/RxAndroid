package com.sunnybear.rxandroid.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.DesignActivity;
import com.sunnybear.rxandroid.presenter.RecyclerFragment;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class CollapsingViewBinder extends ViewBinder<DesignActivity> {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.ctb)
    CollapsingToolbarLayout mCtb;
    @Bind(R.id.tab)
    TabLayout mTab;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.viewpager)
    ViewPager mViewpager;
//    @Bind(R.id.nestedScrollView)
//    NestedScrollView mNestedScrollView;

    public CollapsingViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.acitivity_collapsing;
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
//        mToolbar.setNavigationIcon(R.mipmap.nav_icon_return);
        setSupportActionBar(mToolbar);
        //显示返回按钮图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(view -> ToastUtils.showToastLong(mContext, "点击返回键"));

        mCtb.setTitle("标题");
        //设置CollapsingToolbarLayout扩张时的标题颜色
        mCtb.setExpandedTitleColor(Color.WHITE);
        //设置CollapsingToolbarLayout收缩时的标题颜色
        mCtb.setCollapsedTitleTextColor(Color.WHITE);

        mViewpager.setAdapter(new FragmentPagerAdapter(mPresenter.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return Fragment.instantiate(mContext, RecyclerFragment.class.getName());
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
        mViewpager.setOffscreenPageLimit(3);
        mTab.setupWithViewPager(mViewpager);
        mTab.getTabAt(0).setText("Tab1");
        mTab.getTabAt(1).setText("Tab2");
        mTab.getTabAt(2).setText("Tab3");
    }
}