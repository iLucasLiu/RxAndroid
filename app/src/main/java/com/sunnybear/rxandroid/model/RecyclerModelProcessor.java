package com.sunnybear.rxandroid.model;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.PresenterActivity;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerModelProcessor extends ModelProcessor {

    public RecyclerModelProcessor(PresenterActivity activity) {
        super(activity);
    }

    public List<String> getContent() {
        return Flowable.range(1, 100)
                .map(integer -> "项目:" + integer)
                .toList().blockingGet();
    }
}
