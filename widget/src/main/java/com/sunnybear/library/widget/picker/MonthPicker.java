package com.sunnybear.library.widget.picker;

import android.app.Activity;

import com.sunnybear.library.widget.picker.util.DateUtils;

/**
 * 月份选择器
 * <p/>
 * Created By guchenkai
 */
public class MonthPicker extends OptionPicker {

    public MonthPicker(Activity activity) {
        super(activity, new String[]{});
        for (int i = 1; i <= 12; i++) {
            options.add(DateUtils.fillZore(i));
        }
    }
}