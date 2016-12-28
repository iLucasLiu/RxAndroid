package com.sunnybear.rxandroid.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.TabFragment;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/16.
 */
public class TabViewBinder extends ViewBinder<TabFragment> {

    @Bind(R.id.tv_content)
    TextView mTvContent;

    public TabViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_tab;
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
    }

    @Override
    public void onBindView(Bundle args) {
        super.onBindView(args);
        Logger.e("启动Fragment:" + "Tab" + args.getInt("position"));
        mTvContent.setText("Tab" + args.getInt("position"));
    }
}