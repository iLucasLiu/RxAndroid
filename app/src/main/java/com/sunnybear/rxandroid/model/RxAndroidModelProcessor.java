package com.sunnybear.rxandroid.model;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.rxandroid.model.entity.Person;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/23.
 */
public class RxAndroidModelProcessor extends ModelProcessor {
    public RxAndroidModelProcessor(PresenterActivity activity) {
        super(activity);
    }

    public List<Person> getPersons() {
        final List<String> mobiles = Flowable.fromArray(
                "11111111111",
                "22222222222",
                "33333333333",
                "44444444444",
                "55555555555",
                "66666666666",
                "77777777777",
                "88888888888",
                "99999999999",
                "00000000000")
                .map(s -> "电话号码:" + s)
                .toList().blockingGet();
        return Flowable.fromArray(new Person("Tom", "20"), new Person("Sunny", "21"), new Person("marry", "22"))
                .map(person -> {
                    person.setMobiles(mobiles);
                    return person;
                }).toList().blockingGet();
    }
}