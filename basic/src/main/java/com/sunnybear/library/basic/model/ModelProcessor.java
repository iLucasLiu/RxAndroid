package com.sunnybear.library.basic.model;

import android.content.Context;

import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.basic.presenter.PresenterFragment;

/**
 * model处理器
 * Created by chenkai.gu on 2016/11/17.
 */
public abstract class ModelProcessor implements Model {
    protected Context mContext;
    protected PresenterActivity mActivity;
    protected PresenterFragment mFragment;

    public ModelProcessor(PresenterActivity activity) {
        mContext = mActivity = activity;
    }

    public ModelProcessor(PresenterFragment fragment) {
        mFragment = fragment;
        mContext = fragment.getActivity();
    }
}
