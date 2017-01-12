package com.sunnybear.rxandroid.view;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.pager.lazy.LazyViewPager;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.DesignActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenkai.gu on 2016/12/8.
 */
public class DesignViewBinder extends ViewBinder<DesignActivity> implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tab)
    TabLayout mTab;
    @Bind(R.id.viewpager)
    LazyViewPager mViewpager;

    public DesignViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_design;
    }

    /*@Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        mViewpager.setAdapter(new LazyFragmentPagerAdapter(mPresenter.getSupportFragmentManager()) {
            @Override
            protected Fragment getItem(ViewGroup container, int position) {
                return Fragment.instantiate(mContext, RecyclerFragment.class.getName(),
                        BundleHelper.newInstance().put("position", position).build());
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
            mTab.setupWithViewPager(mViewpager);
        mTab.getTabAt(0).setIcon(R.mipmap.ic_launcher).setText("Tab1");
        mTab.getTabAt(1).setIcon(R.mipmap.ic_launcher).setText("Tab2");
        mTab.getTabAt(2).setIcon(R.mipmap.ic_launcher).setText("Tab3");
    }*/


//        mViewpager.setAdapter(new FragmentPagerAdapter(mPresenter.getSupportFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return Fragment.instantiate(mContext, RecyclerFragment.class.getName());
//            }
//
//            @Override
//            public int getCount() {
//                return 3;
//            }
//        });
//        mViewpager.setOffscreenPageLimit(3);
//        mTab.addTab(mTab.newTab().setText("Tab1"));
//        mTab.addTab(mTab.newTab().setText("Tab2"));
//        mTab.addTab(mTab.newTab().setText("Tab3"));


    @OnClick(R.id.fab)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Snackbar.make(v, "FAB", Snackbar.LENGTH_LONG)
                        .setAction("cancel", v1 -> Logger.i("cancel")).show();
                break;
        }
    }
}