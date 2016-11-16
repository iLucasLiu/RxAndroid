package com.sunnybear.library.basic.service.bind;

import android.app.Service;
import android.os.Binder;

/**
 * 基础Binder
 * Created by chenkai.gu on 2016/10/21.
 */
public abstract class BasicBinder<T extends Service> extends Binder {

    /**
     * 获得绑定Service
     */
    public abstract T getService();
}
