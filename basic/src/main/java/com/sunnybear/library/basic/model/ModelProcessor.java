package com.sunnybear.library.basic.model;

import android.content.Context;

import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.basic.presenter.PresenterFragment;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.RetrofitService;
import com.sunnybear.library.util.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * model处理器
 * Created by chenkai.gu on 2016/11/17.
 */
public class ModelProcessor implements Model {
    protected Context mContext;
    protected PresenterActivity mActivity;
    protected PresenterFragment mFragment;

    public ModelProcessor(PresenterActivity activity) {
        mContext = mActivity = activity;
        getRetrofitService();
    }

    public ModelProcessor(PresenterFragment fragment) {
        mFragment = fragment;
        mContext = fragment.getActivity();
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
}
