package com.sunnybear.library.basic.util;

import android.os.Bundle;

import java.io.Serializable;

/**
 * bundle助手
 * Created by chenkai.gu on 2016/11/11.
 */
public final class BundleHelper {
    private Bundle mBundle;

    public static BundleHelper newInstance() {
        return new BundleHelper();
    }

    private BundleHelper() {
        mBundle = new Bundle();
    }

    public BundleHelper put(String key, String value) {
        mBundle.putString(key, value);
        return this;
    }

    public BundleHelper put(String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public BundleHelper put(String key, Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    public BundleHelper put(String key, Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }

    public Bundle build() {
        return mBundle;
    }
}
