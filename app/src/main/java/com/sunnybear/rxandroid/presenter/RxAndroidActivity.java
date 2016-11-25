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
    private RxAndroidModelProcessor mRxAndroidModelProcessor;

    @Override
    protected RxAndroidViewBinder getViewBinder(Presenter presenter) {
        return new RxAndroidViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
        final StringBuffer result = new StringBuffer();
        final List<Person> persons = mRxAndroidModelProcessor.getPersons();
        Flowable.just(persons)
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
                });
//        Flowable.defer(() -> Flowable.just("Hello RxJava"))
//                .map(s -> s + " --sunnybear")
//                .subscribe(s -> Logger.i(s));
//        long start = System.currentTimeMillis();
//        Flowable.timer(3, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(aLong -> {
//                    ToastUtils.showToastLong(mContext, "定时结束-----" + aLong);
//                    Logger.i("定时结束-----" + (System.currentTimeMillis() - start) + "ms");
//                });
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
        /*Flowable.just("Hello RxJava")
                .map(s -> {
                    Logger.i(Thread.currentThread().getName());
                    int i = 1 / 0;
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
                });*/
    }
}