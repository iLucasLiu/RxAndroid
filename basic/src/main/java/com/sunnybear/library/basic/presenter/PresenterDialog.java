package com.sunnybear.library.basic.presenter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.sunnybear.library.basic.R;
import com.sunnybear.library.basic.view.View;

import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Flowable;

/**
 * 基础Dialog
 * Created by chenkai.gu on 2016/12/15.
 */
public abstract class PresenterDialog<VB extends View> extends Dialog implements Presenter {
    protected Context mContext;
    protected VB mViewBinder;

    private android.view.View mRootView;

    protected PresenterDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initialize(context, cancelable, cancelListener);
    }

    public PresenterDialog(Context context) {
        this(context, R.style.custom_dialog);
        initialize(context, false, null);
    }

    public PresenterDialog(Context context, int themeResId) {
        super(context, themeResId);
        initialize(context, false, null);
    }

    private void initialize(Context context, boolean cancelable, OnCancelListener cancelListener) {
        mContext = context;
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0) throw new RuntimeException("找不到Layout资源,Fragment初始化失败!");
        mRootView = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(mRootView);
        ButterKnife.bind(mViewBinder, this);
        setCancelable(cancelable);
        if (cancelListener != null) setOnCancelListener(cancelListener);
    }

    /**
     * 设置Presenter实例,绑定View
     *
     * @param presenter 自己本身
     */
    protected abstract VB getViewBinder(Presenter presenter);

    @Override
    public void receiveObservableFromView(String tag) {

    }

    @Override
    public Map<String, Flowable> getObservables() {
        return null;
    }

    @Override
    public <T> void sendToView(String tag, T model) {

    }

    @Override
    public <T> void sendToView(String tag, T... models) {

    }

    @Override
    public <T> void sendToView(String tag, Flowable<T> observable) {

    }

    @Override
    public void sendToView(String tag) {

    }
}
