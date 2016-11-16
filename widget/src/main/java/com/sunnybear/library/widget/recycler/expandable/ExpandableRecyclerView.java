package com.sunnybear.library.widget.recycler.expandable;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.sunnybear.library.widget.recycler.BasicRecyclerView;

/**
 * 可扩展RecyclerView
 * Created by chenkai.gu on 2016/11/1.
 */
public class ExpandableRecyclerView extends BasicRecyclerView {
    private ExpandableAdapter.OnExpandCollapseListener mOnExpandCollapseListener;

    public void setOnExpandCollapseListener(ExpandableAdapter.OnExpandCollapseListener onExpandCollapseListener) {
        mOnExpandCollapseListener = onExpandCollapseListener;
    }

    public ExpandableRecyclerView(Context context) {
        this(context, null, 0);
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof ExpandableAdapter))
            throw new RuntimeException("adapter的的类型必须是ExpandableAdapter");
        ExpandableAdapter mAdapter = (ExpandableAdapter) adapter;
        if (mOnExpandCollapseListener != null)
            mAdapter.setOnExpandCollapseListener(mOnExpandCollapseListener);
        super.setAdapter(mAdapter);
    }
}
