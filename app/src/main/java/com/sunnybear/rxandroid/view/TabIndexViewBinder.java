package com.sunnybear.rxandroid.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.util.BundleHelper;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.TabFragment;
import com.sunnybear.rxandroid.presenter.TabIndexActivity;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/16.
 */
public class TabIndexViewBinder extends ViewBinder<TabIndexActivity> {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tab)
    TabLayout mTab;
    @Bind(R.id.vp_content)
    ViewPager mVpContent;
    private FragmentPagerAdapter mAdapter;

    public TabIndexViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tab_index;
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        setSupportActionBar(mToolbar);
        showHideBackIcon(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showToastLong(mContext, "点击返回键");
            }
        });
        mAdapter = new FragmentPagerAdapter(mPresenter.getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return Fragment.instantiate(mContext, TabFragment.class.getName(),
                        BundleHelper.newInstance().put("position", (position + 1)).build());
            }

            @Override
            public int getCount() {
                return 10;
            }
        };
        mVpContent.setAdapter(mAdapter);
        mTab.setupWithViewPager(mVpContent);
        int count = mAdapter.getCount();
        for (int i = 1; i <= count; i++) {
            mTab.getTabAt(i - 1).setText("Tab" + i);
        }
    }
}