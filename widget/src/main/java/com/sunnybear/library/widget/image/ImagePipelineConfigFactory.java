package com.sunnybear.library.widget.image;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

/**
 * Fresco图片加载器配置
 * Created by guchenkai on 2015/9/29.
 */
public final class ImagePipelineConfigFactory {
    /*图片缓存路径*/
    public static String sdCachePath;
    private static ImagePipelineConfig sImagePipelineConfig;

    /**
     * 配置ImagePipelineConfig
     *
     * @param context      context
     * @param okHttpClient okHttp客户端
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context, OkHttpClient okHttpClient) {
        if (sImagePipelineConfig == null)
            sImagePipelineConfig = configureCaches(context, okHttpClient);
        /*启动日志*/
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        return sImagePipelineConfig;
    }

    /**
     * 配置ImagePipelineConfig
     *
     * @param context context
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        return getImagePipelineConfig(context, null);
    }

    /**
     * ImagePipeline配置
     *
     * @param context      context
     * @param okHttpClient okHttp客户端
     * @return ImagePipeline配置实例
     */
    private static ImagePipelineConfig configureCaches(Context context, OkHttpClient okHttpClient) {
        /*内存配置*/
        MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                /*内存缓存中总图片的最大大小,以字节为单位*/
                ConfigConstants.MAX_MEMORY_CACHE_SIZE,
                /*内存缓存中图片的最大数量*/
                Integer.MAX_VALUE,
                /*内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位*/
                ConfigConstants.MAX_MEMORY_CACHE_SIZE,
                /*内存缓存中准备清除的总图片的最大数量*/
                Integer.MAX_VALUE,
                /*内存缓存中单个图片的最大大小*/
                Integer.MAX_VALUE);
        /*修改内存图片缓存数量,空间策略*/
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = () -> bitmapCacheParams;
        /*小图片的磁盘配置*/
        DiskCacheConfig diskSmallCacheConfig = DiskCacheConfig.newBuilder(context)
                /*缓存图片基路径*/
                .setBaseDirectoryPath(new File(sdCachePath).getAbsoluteFile())
                /*文件夹名*/
                .setBaseDirectoryName(ConfigConstants.IMAGE_PIPELINE_SMALL_CACHE_DIR)
                /*日志记录器用于日志错误的缓存*/
//                .setCacheErrorLogger(cacheErrorLogger)
                /*缓存事件侦听器*/
//                .setCacheEventListener(cacheEventListener)
                /*类将包含一个注册表的缓存减少磁盘空间的环境*/
//                .setDiskTrimmableRegistry(diskTrimmableRegistry)
                /*默认缓存的最大大小*/
                .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)
                /*缓存的最大大小,使用设备时低磁盘空间*/
                .setMaxCacheSizeOnLowDiskSpace(ConfigConstants.MAX_SMALL_DISK_CACHE_LOW_SIZE)
                /*缓存的最大大小,当设备极低磁盘空间*/
                .setMaxCacheSizeOnVeryLowDiskSpace(ConfigConstants.MAX_SMALL_DISK_CACHE_VERY_LOW_SIZE)
//                .setVersion(version)
                .build();
        /*默认图片的磁盘配置*/
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                /*缓存图片基路径*/
                .setBaseDirectoryPath(new File(sdCachePath).getAbsoluteFile())
                /*文件夹名*/
                .setBaseDirectoryName(ConfigConstants.IMAGE_PIPELINE_CACHE_DIR)
                /*日志记录器用于日志错误的缓存*/
//                .setCacheErrorLogger(cacheErrorLogger)
                /*缓存事件侦听器*/
//                .setCacheEventListener(cacheEventListener)
                /*类将包含一个注册表的缓存减少磁盘空间的环境*/
//                .setDiskTrimmableRegistry(diskTrimmableRegistry)
                /*默认缓存的最大大小*/
                .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)
                /*缓存的最大大小,使用设备时低磁盘空间*/
                .setMaxCacheSizeOnLowDiskSpace(ConfigConstants.MAX_DISK_CACHE_LOW_SIZE)
                /*缓存的最大大小,当设备极低磁盘空间*/
                .setMaxCacheSizeOnVeryLowDiskSpace(ConfigConstants.MAX_DISK_CACHE_VERY_LOW_SIZE)
//                .setVersion(version)
                .build();
        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = null;
        if (okHttpClient != null)
            configBuilder = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient);
        else
            configBuilder = ImagePipelineConfig.newBuilder(context);
        /*请求日志*/
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        configBuilder
                /*内存缓存配置(一级缓存，已解码的图片)*/
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)
                /*缓存Key工厂*/
//                .setCacheKeyFactory(cacheKeyFactory)
                /*内存缓存和未解码的内存缓存的配置(二级缓存)*/
//                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
                /*线程池配置*/
//                .setExecutorSupplier(executorSupplier)
                /*统计缓存的命中率*/
//                .setImageCacheStatsTracker(imageCacheStatsTracker)
                /*图片解码器配置*/
//                .setImageDecoder(ImageDecoder imageDecoder)
                /*图片预览(缩略图，预加载图等)预加载到文件缓存*/
//                .setIsPrefetchEnabledSupplier(Supplier<Boolean> isPrefetchEnabledSupplier)
                /*磁盘缓存配置(总，三级缓存)*/
                .setMainDiskCacheConfig(diskCacheConfig)
                /*内存用量的缩减,有时我们可能会想缩小内存用量
                  比如应用中有其他数据需要占用内存,不得不把图片缓存清除或者减小或者我们想检查看看手机是否已经内存不够了*/
//                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
                /*自定的网络层配置:如OkHttp,Volley*/
//                .setNetworkFetchProducer(networkFetchProducer)
                /*线程池工厂配置*/
//                .setPoolFactory(poolFactory)
                /*渐进式JPEG图*/
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                /*图片请求监听*/
                .setRequestListeners(requestListeners)
                /*调整和旋转是否支持网络图片*/
                .setResizeAndRotateEnabledForNetwork(true)
                /*图片加载动画*/
//                .setAnimatedImageFactory(AnimatedImageFactory animatedImageFactory)
                /*磁盘缓存配置(小图片，可选～三级缓存的小图优化缓存)*/
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig);
        return configBuilder.build();
    }

    /**
     * 配置常量
     */
    private static class ConfigConstants {
        /*分配内存*/
        private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
        /*使用的缓存数量*/
        public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;

        /*小图极低磁盘空间缓存的最大值(特性:可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图)*/
        public static final int MAX_SMALL_DISK_CACHE_VERY_LOW_SIZE = 5 * ByteConstants.MB;
        /*小图低磁盘空间缓存的最大值(特性:可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图)*/
        public static final int MAX_SMALL_DISK_CACHE_LOW_SIZE = 10 * ByteConstants.MB;
        /*小图磁盘缓存的最大值(特性:可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图)*/
        public static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;

        /*默认图极低磁盘空间缓存的最大值*/
        public static final int MAX_DISK_CACHE_VERY_LOW_SIZE = 10 * ByteConstants.MB;
        /*默认图低磁盘空间缓存的最大值*/
        public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;
        /*默认图磁盘缓存的最大值*/
        public static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;

        /*小图所放路径的文件夹名*/
        public static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small";
        /*默认图所放路径的文件夹名*/
        public static final String IMAGE_PIPELINE_CACHE_DIR = "image";
    }
}
