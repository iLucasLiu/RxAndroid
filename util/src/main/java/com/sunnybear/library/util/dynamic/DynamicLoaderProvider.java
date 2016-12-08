package com.sunnybear.library.util.dynamic;

import android.content.Context;

import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import dalvik.system.DexClassLoader;

/**
 * 动态加载技术提供器
 * <p>需要使用Android工程打出jar并且使用dx工具使用
 * 命令dx --dex --output=<target>.jar <source>.jar编译</p>
 * Created by chenkai.gu on 2016/11/28.
 */
public final class DynamicLoaderProvider {

    /**
     * 动态加载jar/Apk
     *
     * @param context           context
     * @param filePath          被加载的文件路径
     * @param targetPackageName 文件类包名
     * @param <I>               接口泛型
     */
    public static <I> I dynamicLoader(Context context, String filePath, String targetPackageName) {
        I mInterface = null;
        try {
            DexClassLoader loader =
                    new DexClassLoader(filePath, context.getCacheDir().getAbsolutePath(), null, context.getClassLoader());
            Class<?> libProvider = loader.loadClass(targetPackageName);
            mInterface = (I) libProvider.newInstance();
        } catch (Exception e) {
            if (e instanceof FileNotFoundException)
                Logger.e("没有找到加载文件");
            else
                Logger.e(e);
        }
        return mInterface;
    }

    /**
     * 动态加载jar/Apk
     *
     * @param context           context
     * @param file              被加载的文件
     * @param targetPackageName 文件类包名
     * @param <I>               接口泛型
     */
    public static <I> I dynamicLoader(Context context, File file, String targetPackageName) {
        return dynamicLoader(context, file.getAbsolutePath(), targetPackageName);
    }

    /**
     * 动态加载jar/apk
     *
     * @param context           context
     * @param fileName          assets文件名
     * @param filePath          sd卡路径
     * @param targetPackageName 文件类包名
     * @param <I>               接口泛型
     */
    public static <I> I dynamicLoaderFromAssets(Context context, String fileName, String filePath, String targetPackageName) {
        File file = FileUtils.copyAssetsToDisk(context, fileName, filePath);
        return dynamicLoader(context, file, targetPackageName);
    }
}
