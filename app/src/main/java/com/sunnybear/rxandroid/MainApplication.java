package com.sunnybear.rxandroid;

import android.app.Application;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.sunnybear.library.network.NetworkConfiguration;
import com.sunnybear.library.network.OkHttpManager;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.library.widget.image.ImagePipelineConfigFactory;
import com.sunnybear.rxandroid.db.util.DatabaseConfiguration;

import java.io.File;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class MainApplication extends Application {
    private JobManager mJobManager;

    public JobManager getJobManager() {
        return mJobManager;
    }

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
        /*数据库配置*/
        DatabaseConfiguration.initialize(getApplicationContext(), "RxAndroid.db", true
                , (db, oldVersion, newVersion) -> {
                });
        /*Fresco配置*/
        ImagePipelineConfigFactory.sdCachePath = SDCardUtils.getSDCardPath() + "/RxAndroid/cache/image";
        Fresco.initialize(getApplicationContext()
                , ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(getApplicationContext()
                        , OkHttpManager.getInstance().build()));
        mJobManager = new JobManager(new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {
                        Log.v(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)
                .maxConsumerCount(3)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .build());
    }
}
