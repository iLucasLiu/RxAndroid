package com.sunnybear.rxandroid.model;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.Presenter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerModelProcessor extends ModelProcessor {

    public RecyclerModelProcessor(Presenter presenter) {
        super(presenter);
    }

    public List<String> getContent() {
        return Flowable.range(1, 100)
                .map(integer -> "项目:" + integer)
                .toList().blockingGet();
    }

    public List<Position> getPositions() {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Position position = new Position();
            position.setContent("内容" + (i + 1));
            /*position.setPositions(Flowable.range(1, 10)
                    .map(integer -> "position:" + integer)
                    .toList().blockingGet());*/
            positions.add(position);
        }
        return positions;
    }
}
