package com.sunnybear.library.widget.picker;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunnybear.library.widget.picker.util.DateUtils;
import com.sunnybear.library.widget.picker.widget.WheelView;
import com.sunnybear.library.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 日期选择器
 *
 * @since 2015/12/14
 * Created By guchenkai
 */
public class DatePicker extends WheelPicker {
    private OnDatePickListener onDatePickListener;
    private String yearLabel = "年", monthLabel = "月", dayLabel = "日";
    private int startYear = 1970, endYear = 2050;
    private int selectedYear = 0, selectedMonth = 0, selectedDay = 0;
    private Mode mode = Mode.YEAR_MONTH_DAY;
    private String today = "";
    private int todayYear = 0, todayMonth = 0, todayDay = 0;

    public enum Mode {
        //年月日
        YEAR_MONTH_DAY,
        //年月
        YEAR_MONTH,
        //月日
        MONTH_DAY
    }

    public DatePicker(Activity activity) {
        this(activity, Mode.YEAR_MONTH_DAY);
    }

    public DatePicker(Activity activity, Mode mode) {
        super(activity);
        this.mode = mode;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        today = format.format(new Date());
        String[] dates = today.split("-");
        todayYear = Integer.parseInt(dates[0]);
        todayMonth = convertFirstZero(dates[1]);
        todayDay = convertFirstZero(dates[2]);
    }

    /**
     * 转换首位为0的字符串
     *
     * @param s 字符串
     * @return 转换结果
     */
    private int convertFirstZero(String s) {
        if (StringUtils.equals("0", s.substring(0, 1)))
            return Integer.parseInt(s.substring(1, 2));
        return Integer.parseInt(s);
    }

    public void setLabel(String yearLabel, String monthLabel, String dayLabel) {
        this.yearLabel = yearLabel;
        this.monthLabel = monthLabel;
        this.dayLabel = dayLabel;
    }

    /**
     * @see Mode#YEAR_MONTH_DAY
     * @see Mode#YEAR_MONTH
     */
    public void setRange(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public void setOnDatePickListener(OnDatePickListener listener) {
        this.onDatePickListener = listener;
    }

    @Override
    protected View initContentView() {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        WheelView yearView = new WheelView(activity);
        yearView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearView.setTextSize(textSize);
        yearView.setTextColor(textColorNormal, textColorFocus);
        yearView.setLineVisible(lineVisible);
        yearView.setLineColor(lineColor);
        yearView.setOffset(offset);
        layout.addView(yearView);
        TextView yearTextView = new TextView(activity);
        yearTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        yearTextView.setTextSize(textSize);
        yearTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(yearLabel)) {
            yearTextView.setText(yearLabel);
        }
        layout.addView(yearTextView);
        WheelView monthView = new WheelView(activity);
        monthView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthView.setTextSize(textSize);
        monthView.setTextColor(textColorNormal, textColorFocus);
        monthView.setLineVisible(lineVisible);
        monthView.setLineColor(lineColor);
        monthView.setOffset(offset);
        layout.addView(monthView);
        TextView monthTextView = new TextView(activity);
        monthTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        monthTextView.setTextSize(textSize);
        monthTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        layout.addView(monthTextView);
        final WheelView dayView = new WheelView(activity);
        dayView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayView.setTextSize(textSize);
        dayView.setTextColor(textColorNormal, textColorFocus);
        dayView.setLineVisible(lineVisible);
        dayView.setLineColor(lineColor);
        dayView.setOffset(offset);
        layout.addView(dayView);
        TextView dayTextView = new TextView(activity);
        dayTextView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        dayTextView.setTextSize(textSize);
        dayTextView.setTextColor(textColorFocus);
        if (!TextUtils.isEmpty(dayLabel)) {
            dayTextView.setText(dayLabel);
        }
        layout.addView(dayTextView);
        if (mode.equals(Mode.YEAR_MONTH)) {
            dayView.setVisibility(View.GONE);
            dayTextView.setVisibility(View.GONE);
        } else if (mode.equals(Mode.MONTH_DAY)) {
            yearView.setVisibility(View.GONE);
            yearTextView.setVisibility(View.GONE);
        }
        if (!mode.equals(Mode.MONTH_DAY)) {
            if (!TextUtils.isEmpty(yearLabel)) {
                yearTextView.setText(yearLabel);
            }
            ArrayList<String> years = new ArrayList<String>();
            for (int i = startYear; i <= endYear; i++) {
                years.add(String.valueOf(i));
            }
            yearView.setItems(years);
            yearView.setSelection(todayYear - startYear);//TODO:设置年默认值
            yearView.setOnWheelViewListener((selectedIndex, item) -> {
                selectedYear = stringToYearMonthDay(item);
                //需要根据年份及月份动态计算天数
                int maxDays = DateUtils.calculateDaysInMonth(selectedYear, selectedMonth);
                ArrayList<String> days = new ArrayList<>();
                for (int i = 1; i <= maxDays; i++) {
                    days.add(DateUtils.fillZore(i));
                }
                dayView.setItems(days);
                dayView.startScrollerTask();
            });
        }
        if (!TextUtils.isEmpty(monthLabel)) {
            monthTextView.setText(monthLabel);
        }
        ArrayList<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(DateUtils.fillZore(i));
        }
        monthView.setItems(months);
        monthView.setSelection(todayMonth - 1);//TODO:设置月默认值
        monthView.setOnWheelViewListener((selectedIndex, item) -> {
            selectedMonth = stringToYearMonthDay(item);
            //需要根据年份及月份动态计算天数
            int maxDays = DateUtils.calculateDaysInMonth(selectedYear, selectedMonth);
            ArrayList<String> days = new ArrayList<>();
            for (int i = 1; i <= maxDays; i++) {
                days.add(DateUtils.fillZore(i));
            }
            //TODO:设置天默认值
            if (selectedMonth == todayMonth)
                dayView.setItems(days, todayDay - 1);
            else
                dayView.setItems(days);
//                dayView.setSelection(todayDay - 1);
//                dayView.startScrollerTask();
        });
        if (!mode.equals(Mode.YEAR_MONTH)) {
            if (!TextUtils.isEmpty(dayLabel)) {
                dayTextView.setText(dayLabel);
            }
            //年月日选择时，最大天数根据年月来计算
            int maxDays;
            if (mode.equals(Mode.YEAR_MONTH_DAY)) {
                maxDays = DateUtils.calculateDaysInMonth(selectedYear, selectedMonth);
            } else {
                maxDays = DateUtils.calculateDaysInMonth(selectedMonth);
            }
            ArrayList<String> days = new ArrayList<>();
            for (int i = 1; i <= maxDays; i++) {
                days.add(DateUtils.fillZore(i));
            }
            dayView.setItems(days);
            dayView.setOnWheelViewListener((selectedIndex, item) -> {
                if (TextUtils.isEmpty(item)) {
                    return;
                }
                selectedDay = stringToYearMonthDay(item);
            });
        }
        return layout;
    }

    private int stringToYearMonthDay(String text) {
        if (text.startsWith("0"))
            //截取掉前缀0以便转换为整数
            text = text.substring(1);
        return Integer.parseInt(text);
    }

    @Override
    protected void setContentViewAfter(View contentView) {
        super.setContentViewAfter(contentView);
        super.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public void onConfirm() {
                if (onDatePickListener != null) {
                    String day = DateUtils.fillZore(selectedDay);
                    String month = DateUtils.fillZore(selectedMonth);
                    String year = String.valueOf(selectedYear);
                    switch (mode) {
                        case YEAR_MONTH:
                            ((OnYearMonthPickListener) onDatePickListener).onDatePicked(year, month);
                            break;
                        case MONTH_DAY:
                            ((OnMonthDayPickListener) onDatePickListener).onDatePicked(month, day);
                            break;
                        default:
                            ((OnYearMonthDayPickListener) onDatePickListener).onDatePicked(year, month, day);
                            break;
                    }
                }
            }
        });
    }

    protected interface OnDatePickListener {

    }

    public interface OnYearMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month, String day);
    }

    public interface OnYearMonthPickListener extends OnDatePickListener {

        void onDatePicked(String year, String month);
    }

    public interface OnMonthDayPickListener extends OnDatePickListener {

        void onDatePicked(String month, String day);
    }
}
