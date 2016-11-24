package com.sunnybear.rxandroid.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sunnybear.library.basic.model.InjectModel;
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
    @InjectModel(RxAndroidModelProcessor.class)
    private RxAndroidModelProcessor mRxAndroidModelProcessor;

    @Override
    protected RxAndroidViewBinder getViewBinder(Context context) {
        return new RxAndroidViewBinder(context);
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
        /*Flowable.just(persons)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<List<Person>, Publisher<Person>>() {
                    @Override
                    public Publisher<Person> apply(List<Person> persons) throws Exception {
                        return Flowable.fromIterable(persons);
                    }
                })
                .filter(new Predicate<Person>() {
                    @Override
                    public boolean test(Person person) throws Exception {
                        Flowable.fromIterable(person.getMobiles())
                                .doOnNext(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        Logger.i(Thread.currentThread().getName());
                                        Logger.d(s);
                                    }
                                }).subscribe();
                        return true;
                    }
                })
                .map(new Function<Person, String>() {
                    @Override
                    public String apply(Person person) throws Exception {
                        return "name:" + person.getName() + "--------" + "age:" + person.getAge();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        result.append(s).append("\n");
                        send("mobile", result.toString());
                    }
                });*/
    }
}