package com.sunnybear.library.widget.picker;

import android.app.Activity;

import com.sunnybear.library.widget.picker.util.DateUtils;

/**
 * 分钟选择器
 * <p/>
 * Created By guchenkai
 */
public class MinutePicker extends OptionPicker {

    public MinutePicker(Activity activity) {
        super(activity, new String[]{});
        for (int i = 0; i < 60; i++) {
            options.add(DateUtils.fillZore(i));
        }
    }
}