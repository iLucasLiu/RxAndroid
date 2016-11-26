package com.sunnybear.library.network.service.bind;

import android.app.Service;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sunnybear.library.util.Logger;

/**
 * 泛型型Service绑定连接器
 * Created by chenkai.gu on 2016/10/21.
 */
public class GenericServiceConnection<S extends Service, B extends BasicBinder> implements ServiceConnection {
    private S mService;
    private B mBinder;

    /**
     * 获得Service
     */
    public S getService() {
        return mService;
    }

    /**
     * Service绑定时调用
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Logger.d("已绑定service");
        mBinder = (B) service;
        mService = (S) mBinder.getService();
    }

    /**
     * Service断开时调用
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Logger.d("service绑定已断开");
    }
}
