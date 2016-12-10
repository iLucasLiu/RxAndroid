package com.sunnybear.library.network;

import android.content.Context;

/**
 * 网络参数配置
 * Created by chenkai.gu on 2016/4/13.
 */
public class NetworkConfiguration {
    private static Context mContext;
    private static String mNetworkCacheDirectoryPath;
    private static int mNetworkCacheSize;

    public static int CONNECT_TIMEOUT_MILLIS;//连接时间超时
    public static int WRITE_TIMEOUT_MILLIS;//写入时间超时
    public static int READ_TIMEOUT_MILLIS;//读取时间超时

//    public static String CERTIFICATE_NAME;//证书名

    private static int[] mCertificates;//证书路径{R.raw.XXX}

    public static void configuration(Context context,
                                     int connectTimeoutMillis,
                                     int writeTimeoutMillis,
                                     int readTimeoutMillis,
                                     String networkCacheDirectoryPath, int networkCacheSize,
                                     int... certificates) {
        mContext = context;
        mNetworkCacheDirectoryPath = networkCacheDirectoryPath;
        mNetworkCacheSize = networkCacheSize;

        CONNECT_TIMEOUT_MILLIS = connectTimeoutMillis;
        WRITE_TIMEOUT_MILLIS = writeTimeoutMillis;
        READ_TIMEOUT_MILLIS = readTimeoutMillis;
        mCertificates = certificates;
    }

    public static Context getContext() {
        return mContext;
    }

    public static String getNetworkCacheDirectoryPath() {
        return mNetworkCacheDirectoryPath;
    }

    public static int getNetworkCacheSize() {
        return mNetworkCacheSize;
    }

    public static int[] getCertificates() {
        return mCertificates;
    }
}
