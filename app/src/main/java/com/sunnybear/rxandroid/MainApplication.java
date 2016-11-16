package com.sunnybear.rxandroid;

import android.app.Application;

import com.sunnybear.library.network.NetworkConfiguration;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;

import java.io.File;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*日志配置*/
        Logger.init("RxAndroid").hideThreadInfo().setLogLevel(Logger.LogLevel.FULL);
        /*网络配置*/
        NetworkConfiguration.configuration(getApplicationContext(),
                15 * 1000, 20 * 1000, 20 * 1000, "",
                SDCardUtils.getSDCardPath() + File.separator + "RxAndroid",
                100 * 1024 * 1024);
//        RetrofitProvider.initialize("http://10.103.18.196:8089/SFAInterface/");
        RetrofitProvider.initialize("RxAndroid", "http://baike.baidu.com/api/openapi/");
    }
}
