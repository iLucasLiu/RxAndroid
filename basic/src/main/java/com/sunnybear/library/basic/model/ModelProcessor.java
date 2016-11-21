package com.sunnybear.library.basic.model;

import android.content.Context;

import com.sunnybear.library.basic.presenter.PresenterActivity;

/**
 * model处理器
 * Created by chenkai.gu on 2016/11/17.
 */
public abstract class ModelProcessor implements Model {
    protected PresenterActivity mPresenter;

    public ModelProcessor(Context context) {
        mPresenter = (PresenterActivity) context;
        if (!(mPresenter instanceof PresenterActivity))
            throw new RuntimeException("ViewBinder中的Content必须是PresenterActivity类型");
    }
}
