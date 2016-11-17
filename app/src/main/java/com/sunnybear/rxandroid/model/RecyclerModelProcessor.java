package com.sunnybear.rxandroid.model;

import android.content.Context;

import com.sunnybear.library.basic.model.ModelProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerModelProcessor extends ModelProcessor {
    public RecyclerModelProcessor(Context context) {
        super(context);
    }

    public List<String> getContent() {
        List<String> strings = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            strings.add("项目" + i);
        }
        return strings;
    }
}
