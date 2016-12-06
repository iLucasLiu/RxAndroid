package com.sunnybear.rxandroid.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.Logger;
import com.sunnybear.rxandroid.model.RxAndroidModelProcessor;
import com.sunnybear.rxandroid.model.entity.Person;
import com.sunnybear.rxandroid.view.RxAndroidViewBinder;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chenkai.gu on 2016/11/23.
 */
public class RxAndroidActivity extends PresenterActivity<RxAndroidViewBinder> {
    @InjectModel
    RxAndroidModelProcessor mRxAndroidModelProcessor;

    @Override
    protected RxAndroidViewBinder getViewBinder(Presenter presenter) {
        return new RxAndroidViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        final List<Person> persons = mRxAndroidModelProcessor.getPersons();
        /*Flowable.just(persons)
                .observeOn(Schedulers.io())
                .flatMap(persons1 -> Flowable.fromIterable(persons1))
                .filter(person -> {
                    Flowable.fromIterable(person.getMobiles())
                            .doOnNext(s -> {
                                Logger.i(Thread.currentThread().getName());
                                Logger.d(s);
                            }).subscribe();
                    return true;
                })
                .map(person -> "name:" + person.getName() + "-------age:" + person.getAge())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    result.append(s).append("\n");
                    send("mobile", result.toString());
                });*/
//        Flowable.create(e -> {
//            Logger.e("create:" + Thread.currentThread().getName());
//            Flowable.just("Hello RxJava")
//                    .map(s -> {
//                        Logger.e("sub map:" + Thread.currentThread().getName());
//                        Logger.i(s);
//                        return s;
//                    })
//                    .doOnNext(s -> Logger.e("sub doOnNext:" + Thread.currentThread().getName()))
//                    .doOnComplete(() -> Logger.e("sub doOnComplete:" + Thread.currentThread().getName())).subscribe();
//            e.onNext("123");
//            e.onComplete();
//        }, BackpressureStrategy.BUFFER)
//                .compose(upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
//                .doOnNext(o -> Logger.e("doOnNext:" + Thread.currentThread().getName()))
//                .doOnComplete(() -> Logger.e("doOnComplete:" + Thread.currentThread().getName()))
//                .subscribe();
//        Flowable.defer(() -> Flowable.just("Hello RxJava"))
//                .map(s -> s + " --sunnybear")
//                .subscribe(s -> Logger.i(s));
//        Logger.i("定时任务开始");
//        long start = System.currentTimeMillis();
//        Flowable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
//                .doOnComplete(() -> {
//                    Logger.e(Thread.currentThread().getName());
//                    Logger.i("定时任务结束-----" + (System.currentTimeMillis() - start) + "ms");
//                }).subscribe();
//        Flowable.just("Hello RxJava")
//                .repeat(5)
//                .subscribe(s -> Logger.i(s),
//                        throwable -> Logger.e(throwable.getMessage()),
//                        () -> Logger.w("完成"));
//        Flowable.just("Hello RxJava")
//                .repeatWhen(objectFlowable -> objectFlowable
//                        .zipWith(Flowable.range(1, 3), (o, integer) -> integer)
//                        .flatMap(integer -> Flowable.timer(1, TimeUnit.SECONDS)))
//                .subscribe(s -> Logger.i(s),
//                        throwable -> Logger.e(throwable.getMessage()),
//                        () -> Logger.w("完成"));
        //倒计时
//        int startTime = 20;
//        int finalStartTime = startTime;
//        Flowable.interval(0, 1, TimeUnit.SECONDS)
//                .take(startTime++)
//                .map(time -> finalStartTime - time)
////                .toObservable()
//                .subscribe(aLong -> Logger.d(String.format("倒计时: %s s", aLong)),
//                        throwable -> Logger.e("倒计时错误"),
//                        () -> Logger.i("倒计时结束"));
        Flowable.just("Hello RxJava")
                .map(s -> {
                    Logger.i(Thread.currentThread().getName());
//                    int i = 1 / 0;
                    return s;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    Logger.i(Thread.currentThread().getName());
                    return Flowable.just("错误:" + throwable.getMessage());
                })
                .subscribe(s -> {
                    Logger.i(Thread.currentThread().getName());
                    Logger.d(s);
                });
//        Flowable.just(1, 2, 3).repeatWhen(objectFlowable -> {
//            Logger.e(Thread.currentThread().getName());
//            //重复3次
//            return objectFlowable.zipWith(Flowable.range(1, 5), (o, integer) -> integer)
//                    .flatMap((Function<Integer, Publisher<?>>) integer -> {
//                        Logger.d("delay repeat the " + integer + " count");
//                        //1秒钟重复一次
//                        return Flowable.timer(1, TimeUnit.SECONDS);
//                    });
//        }).subscribe(integer -> Logger.i("Next:" + integer),
//                throwable -> Logger.e("Error:" + throwable.getMessage()),
//                () -> Logger.i("Sequence complete."));
//        Flowable.interval(1, TimeUnit.SECONDS)
//                .take(10)
//                .groupBy(value -> value % 3)
//                .subscribe(result1 ->
////                        result1.subscribe(value -> Logger.i("key:" + result1.getKey() + ",value:" + value)));
//        Flowable.just("s", "u", "n", "n", "y")
//                .scan((s, s2) -> s + s2)
//                .doOnNext(s -> Logger.d(s))
//                .subscribe(s -> Logger.i(s));
//        Flowable.just(1, 2, 3, "RxJava", "RxAndroid")
//                .ofType(String.class)
//                .doOnNext(s -> Logger.i("数字:" + s)).subscribe();
//        Flowable.zip(Flowable.just(1, 2, 3), Flowable.just("RxJava", "RxAndroid"),
//                (integer, s) -> {
//                    Logger.i("数字:" + integer);
//                    Logger.i("字符串:" + s);
//                    return s + integer;
//                }).subscribe();
//        Flowable.just(1, 2, 3).repeatWhen(objectFlowable -> objectFlowable.zipWith(Flowable.range(1, 5),
//                (BiFunction<Object, Integer, Object>) (o, integer) -> {
//                    Logger.e("第" + integer + "发射");
//                    return integer;
//                }).flatMap((Function<Object, Publisher<?>>) o -> Flowable.timer(1, TimeUnit.SECONDS)))
//                .subscribe(integer -> Logger.d(integer.toString()));
        /*final long start = System.currentTimeMillis();
        Flowable.just("RxAndroid").zipWith(Flowable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread()),
                (s, aLong) -> {
                    Logger.e(Thread.currentThread().getName());
                    Logger.i("定时:" + (System.currentTimeMillis() - start) + "ms");
                    return s;
                })
                .doOnNext(s -> Logger.i(s))
                .doOnComplete(() -> Logger.i("完成")).subscribe();*/
    }
}