package com.sunnybear.library.util;

import android.content.Context;

import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.LogLevel;

import java.util.List;

/**
 * 偏好设置文件工具
 * Created by guchenkai on 2015/11/2.
 */
public final class PreferenceHelper {

    public static void init(Context context, String password, LogLevel logLevel) {
        Hawk.init(context, password, logLevel);
    }

    public static void init(Context context, String password) {
        Hawk.init(context, password);
    }

    /**
     * 保存编号设置数据
     *
     * @param key   key
     * @param value 数据
     * @param <T>   类型
     */
    public static <T> void save(String key, T value) {
        Hawk.put(key, value);
    }

    /**
     * 保存编号设置数据集合
     *
     * @param key    key
     * @param values 数据集合
     * @param <T>    类型
     */
    public static <T> void save(String key, List<T> values) {
        Hawk.put(key, values);
    }

    /**
     * 获得偏好设置数据
     *
     * @param key      key
     * @param defValue 默认值
     * @param <T>      类型
     * @return 偏好设置数据
     */
    public static <T> T getValue(String key, T defValue) {
        return Hawk.get(key, defValue);
    }

    /**
     * 根据key删除偏好设置数据
     *
     * @param key key
     */
    public static void remove(String key) {
        Hawk.remove(key);
    }

    /**
     * 清空偏好设置文件
     */
    public static void clear() {
        Hawk.clear();
    }

    /**
     * 根据key比对是否存在key对应的数据
     *
     * @param key key
     * @return 是否存在
     */
    public static boolean contains(String key) {
        return Hawk.contains(key);
    }
}
