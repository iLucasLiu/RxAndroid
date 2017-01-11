package com.sunnybear.library.util;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * RxJava插件
 * Created by chenkai.gu on 2016/12/16.
 */
public final class RxPlugin {

    /**
     * 切换线程
     *
     * @param observableThread 观察者线程
     * @param <Upstream>       输入泛型
     * @param <Downstream>     输出泛型
     */
    public static <Upstream, Downstream> FlowableTransformer<Upstream, Downstream> switchThread(Scheduler observableThread) {
        return upstream -> (Publisher<Downstream>) upstream
                .subscribeOn(observableThread)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 遍历List之后发射出单个元素
     *
     * @param <T> 元素泛型
     */
    public static <T> FlowableTransformer<List<T>, T> traversal() {
        return upstream -> upstream
                .filter(ts -> ts != null)
                .flatMap(ts -> Flowable.fromIterable(ts));
    }

    /**
     * 错误处理
     *
     * @param <Upstream>   输入泛型
     * @param <Downstream> 输出泛型
     */
    public static <Upstream, Downstream> FlowableTransformer<Upstream, Downstream> error() {
        return upstream -> (Publisher<Downstream>) upstream
                .onErrorReturn(throwable -> null);
    }

    /**
     * 错误处理
     *
     * @param error        发生错误后的输入发射源
     * @param <Upstream>   输入泛型
     * @param <Downstream> 输出泛型
     */
    public static <Upstream, Downstream> FlowableTransformer<Upstream, Downstream> error(Flowable<Upstream> error) {
        return upstream -> (Publisher<Downstream>) upstream
                .onErrorResumeNext((Function<Throwable, Publisher<? extends Upstream>>) throwable -> error);
    }

    /**
     * 判断发射源是否不为空
     *
     * @param <T> 发射源泛型
     */
    public static <T> FlowableTransformer<T, T> isNotNull() {
        return upstream -> upstream.filter(t -> t != null);
    }

    /**
     * 定时器
     *
     * @param second      定时秒数
     * @param timerThread 定时器工作线程
     */
    public static void timer(int second, Scheduler timerThread, Runnable runnable) {
        Flowable.timer(second, TimeUnit.SECONDS, timerThread)
                .doOnComplete(() -> runnable.run()).subscribe();
    }

    /**
     * 定时器,工作在Android主线程
     *
     * @param second 定时秒数
     */
    public static void timer(int second, Runnable runnable) {
        timer(second, AndroidSchedulers.mainThread(), runnable);
    }
}
