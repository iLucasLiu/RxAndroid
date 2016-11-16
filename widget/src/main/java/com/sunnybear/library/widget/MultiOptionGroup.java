package com.sunnybear.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.sunnybear.library.util.DensityUtil;
import com.sunnybear.library.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 多选选择器
 * Created by chenkai.gu on 2016/11/2.
 */
public class MultiOptionGroup extends LinearLayout implements CheckBox.OnCheckedChangeListener {
    private static final String TAG = MultiOptionGroup.class.getSimpleName();
    private static final String[] titles = {"A.", "B.", "C.", "D.", "E.", "F.", "G.", "H.", "I.", "J.", "K.", "L.", "M.", "N."
            , "O.", "P.", "Q.", "R.", "S.", "T.", "U.", "V.", "W.", "X.", "Y.", "Z."};
    /*选项之间的间距*/
    private int margin;
    /*字体大小*/
    private float textSize;
    /*字体颜色*/
    private int textColor;
    /*已选择的选项*/
    private List<String> selOptions;
    /*选择器贴图*/
    private Drawable selectDrawable;
    /*选项选择监听器*/
    private OnMultiSelectedListener mOnMultiSelectedListener;

    public void setOnMultiSelectedListener(OnMultiSelectedListener onMultiSelectedListener) {
        mOnMultiSelectedListener = onMultiSelectedListener;
    }

    public MultiOptionGroup(Context context) {
        this(context, null);
    }

    public MultiOptionGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyleable(context, attrs);

        selOptions = new ArrayList<>();
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MultiOptionGroup);
        margin = array.getDimensionPixelSize(R.styleable.MultiOptionGroup_mg_margin, 0);
        textSize = array.getDimension(R.styleable.MultiOptionGroup_mg_textSize, DensityUtil.px2sp(getContext(), 14));
        textColor = array.getColor(R.styleable.MultiOptionGroup_mg_textColor, -1);
        selectDrawable = array.getDrawable(R.styleable.MultiOptionGroup_mg_selectDrawable);
        array.recycle();
    }

    /**
     * 设置选项
     *
     * @param options 选项集合
     */
    public void setOptions(List<String> options) {
        for (int i = 0; i < options.size(); i++) {
            CheckBox button = new CheckBox(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i != options.size() - 1)
                switch (getOrientation()) {
                    case HORIZONTAL:
                        params.setMargins(0, 0, margin, 0);
                        break;
                    case VERTICAL:
                        params.setMargins(0, 0, 0, margin);
                        break;
                }
            button.setText(titles[i] + options.get(i));
            button.setTextSize(textSize);
            if (textColor != -1)
                button.setTextColor(textColor);
            if (selectDrawable != null)
                button.setButtonDrawable(selectDrawable);
            button.setOnCheckedChangeListener(this);
            addView(button, params);
        }
    }

    public void setOptions(String... options) {
        List<String> ops = Arrays.asList(options);
        setOptions(ops);
    }

    private String getOption(String s) {
        String[] strings = s.split("\\.");
        return strings[1];
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String option = getOption(buttonView.getText().toString());
        if (isChecked)
            selOptions.add(option);
        else
            selOptions.remove(option);
        String options = getOptions(selOptions);
        Logger.d(TAG, "选择的选项:" + options);
        if (mOnMultiSelectedListener != null)
            mOnMultiSelectedListener.onMultiSelected(selOptions);
    }

    private String getOptions(List<String> options) {
        StringBuffer buffer = new StringBuffer();
        for (String option : options) {
            buffer.append(option).append(",");
        }
        int length = buffer.length();
        if (length > 0)
            return buffer.substring(0, length - 1).toString();
        else
            return "";
    }

    /**
     * 选项选择监听器
     */
    public interface OnMultiSelectedListener {

        void onMultiSelected(List<String> options);
    }
}
