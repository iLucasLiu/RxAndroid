package com.sunnybear.rxandroid;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.hawk.LogLevel;
import com.sunnybear.library.basic.util.CrashHandler;
import com.sunnybear.library.network.NetworkConfiguration;
import com.sunnybear.library.network.OkHttpManager;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.PreferenceHelper;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.library.widget.image.ImagePipelineConfigFactory;
import com.sunnybear.rxandroid.db.util.DatabaseConfiguration;

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
                15 * 1000, 20 * 1000, 20 * 1000, SDCardUtils.getSDCardPath() + File.separator + "RxAndroid",
                100 * 1024 * 1024, R.raw.root);
//        RetrofitProvider.initialize("http://10.103.18.196:8089/SFAInterface/");
        RetrofitProvider.initialize("RxAndroid", "https://10.103.18.196/SFAInterface/");
        /*数据库配置*/
        DatabaseConfiguration.initialize(getApplicationContext(), "RxAndroid.db", true
                , (db, oldVersion, newVersion) -> {
                });
        /*Fresco配置*/
        ImagePipelineConfigFactory.sdCachePath = SDCardUtils.getSDCardPath() + "/RxAndroid/cache/image";
        Fresco.initialize(getApplicationContext()
                , ImagePipelineConfigFactory.getImagePipelineConfig(getApplicationContext()
                        , OkHttpManager.getInstance().build()));
        /*Preference设置*/
        PreferenceHelper.init(getApplicationContext(), "RxAndroid", LogLevel.FULL);
        /*异常日志*/
        CrashHandler.getInstance().setCrashHandler(getApplicationContext())
                .setSavePath(SDCardUtils.getSDCardPath() + "/nielsen/crash/");
    }
}
