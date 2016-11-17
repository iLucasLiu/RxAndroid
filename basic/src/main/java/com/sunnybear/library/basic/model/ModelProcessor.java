package com.sunnybear.library.basic.model;

import android.content.Context;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.eventbus.EventBusHelper;

/**
 * model处理器
 * Created by chenkai.gu on 2016/11/17.
 */
public abstract class ModelProcessor<P extends Presenter> implements Model {
    protected Context mContext;
    protected P mPresenter;

    public ModelProcessor(Context context) {
        mContext = context;
        mPresenter = (P) context;
        if (!(mPresenter instanceof PresenterActivity))
            throw new RuntimeException("ViewBinder中的Content必须是PresenterActivity类型");
        EventBusHelper.register(this);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        mPresenter = null;
        mContext = null;
        EventBusHelper.unregister(this);
    }

    @Override
    public void onRestart() {

    }
}
