package com.sunnybear.library.basic.presenter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.sunnybear.library.basic.R;
import com.sunnybear.library.basic.view.View;

import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Flowable;

/**
 * 基础PopupWindow
 * Created by chenkai.gu on 2016/12/15.
 */
public abstract class PresenterPopupWindow<VB extends View> extends PopupWindow implements Presenter, android.view.View.OnTouchListener, android.view.View.OnKeyListener {
    protected Context mContext;
    protected VB mViewBinder;
    private android.view.View mRootView;
    private ViewGroup mMainLayout;

    public PresenterPopupWindow(Context context) {
        super(context);
        mContext = context;
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0) throw new RuntimeException("找不到Layout资源,Fragment初始化失败!");
        mRootView = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(mRootView);//设置布局
        ButterKnife.bind(this, mRootView);
        mMainLayout = (ViewGroup) mRootView.findViewById(R.id.popup_layout);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);//设置宽
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//设置高
        setFocusable(true);// 设置PopupWindow可获得焦点
        setTouchable(true); // 设置PopupWindow可触摸
        setOutsideTouchable(true);// 设置非PopupWindow区域可触摸
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        setBackgroundDrawable(dw);//设置背景
        //点击PopupWindow之外的区域关闭PopupWindow
        mRootView.setOnTouchListener(this);
        //响应返回键
        mRootView.setOnKeyListener(this);
    }

    @Override
    public boolean onTouch(android.view.View v, MotionEvent event) {
        int height = mMainLayout.getHeight();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP && y > height)
            dismiss();
        return true;
    }

    @Override
    public boolean onKey(android.view.View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
            dismiss();
        return false;
    }

    /**
     * 设置layout在PopupWindow中显示的位置
     *
     * @param target 目标view
     */
    public void show(android.view.View target) {
        showAtLocation(target, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
