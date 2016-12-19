package com.sunnybear.library.basic.model;

import android.content.Context;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.basic.presenter.PresenterFragment;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.RetrofitService;
import com.sunnybear.library.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.reactivex.Flowable;

/**
 * model处理器
 * Created by chenkai.gu on 2016/11/17.
 */
public class ModelProcessor implements Model {
    protected Context mContext;
    private Presenter mPresenter;
    private PresenterActivity mActivity;
    private PresenterFragment mFragment;

    public ModelProcessor(Presenter presenter) {
        mPresenter = presenter;
        if (presenter instanceof PresenterActivity) {
            mContext = mActivity = (PresenterActivity) presenter;
        } else if (presenter instanceof PresenterFragment) {
            mFragment = (PresenterFragment) presenter;
            mContext = mFragment.getContext();
        }
        getRetrofitService();
    }

    /**
     * 获得RetrofitService实例
     *
     * @param <I> RetrofitService实例
     */
    private <I> void getRetrofitService() {
        I retrofitService;
        try {
            Class<?> self = this.getClass();
            Field[] fields = self.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(RetrofitService.class);
                if (annotation != null) {
                    if (field.getModifiers() != Modifier.PUBLIC) Logger.e("RetrofitService不能使用修饰符");
                    Class<I> sc = (Class<I>) field.getType();
                    retrofitService = RetrofitProvider.create(sc);
                    field.setAccessible(true);
                    field.set(this, retrofitService);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送观察者
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void sendToView(String tag, T model) {
        if (mPresenter instanceof PresenterActivity)
            mActivity.sendToView(tag, model);
        else if (mPresenter instanceof PresenterFragment)
            mFragment.sendToView(tag, model);
    }

    /**
     * 发送观察者
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void sendToView(String tag, T... models) {
        if (mPresenter instanceof PresenterActivity)
            mActivity.sendToView(tag, models);
        else if (mPresenter instanceof PresenterFragment)
            mFragment.sendToView(tag, models);
    }

    /**
     * 发送观察者
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void sendToView(String tag, Flowable<T> observable) {
        if (mPresenter instanceof PresenterActivity)
            mActivity.sendToView(tag, observable);
        else if (mPresenter instanceof PresenterFragment)
            mFragment.sendToView(tag, observable);
    }

    /**
     * 发送一个动作
     *
     * @param tag 标签
     */
    public final void sendToView(String tag) {
        if (mPresenter instanceof PresenterActivity)
            mActivity.sendToView(tag);
        else if (mPresenter instanceof PresenterFragment)
            mFragment.sendToView(tag);
    }
}
