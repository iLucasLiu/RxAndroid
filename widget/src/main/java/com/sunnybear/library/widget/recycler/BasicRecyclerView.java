package com.sunnybear.library.widget.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.sunnybear.library.widget.R;
import com.sunnybear.library.widget.recycler.adapter.BasicAdapter;
import com.sunnybear.library.widget.recycler.listener.OnItemClickListener;
import com.sunnybear.library.widget.recycler.listener.OnItemLongClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

/**
 * 提供基本处理的RecyclerView
 * Created by guchenkai on 2015/10/29.
 */
public class BasicRecyclerView extends RecyclerView {
    public static final int NONE = -1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int INVALID_OFFSET = 2;
    public static final int GRID = 3;

    private LayoutManager manager;
    private int layout_mode;//布局方式
    private int column_num;//布局方式为grid时,每行的显示数
    private boolean has_row_divider;//是否显示行间隔线
    private boolean has_rank_divider;//是否显示列间距
    private int divider_width;//列间隔线的宽度
    private int divider_height;//行间隔线的高度
    private int divider_color;//间隔线的颜色
    private int divider_marginLeft, divider_marginRight, divider_marginTop, divider_marginBottom;
    private boolean isStartAnimator;
    private int mDuration;//动画持续时间
    private boolean isFirstOnly;

    public BasicRecyclerView(Context context) {
        this(context, null, 0);
    }

    public BasicRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initStyleable(context, attrs);
        initView(context);
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BasicRecyclerView);
        layout_mode = array.getInt(R.styleable.BasicRecyclerView_layout_mode, VERTICAL);
        column_num = array.getInt(R.styleable.BasicRecyclerView_column_num, 3);
        has_row_divider = array.getBoolean(R.styleable.BasicRecyclerView_has_row_divider, false);
        has_rank_divider = array.getBoolean(R.styleable.BasicRecyclerView_has_rank_divider, false);

        divider_width = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_width, 1f);
        divider_height = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_height, 1f);
        divider_marginLeft = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_marginLeft, 0f);
        divider_marginRight = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_marginRight, 0f);
        divider_marginTop = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_marginTop, 0f);
        divider_marginBottom = (int) array.getDimension(R.styleable.BasicRecyclerView_divider_marginBottom, 0f);
        divider_color = array.getColor(R.styleable.BasicRecyclerView_divider_color, Color.parseColor("#00000000"));
        isStartAnimator = array.getBoolean(R.styleable.BasicRecyclerView_is_start_animator, false);
        mDuration = array.getInt(R.styleable.BasicRecyclerView_duration_animator, 300);
        isFirstOnly = array.getBoolean(R.styleable.BasicRecyclerView_is_first_only, false);
        array.recycle();
    }

    /**
     * 初始化view
     */
    private void initView(Context context) {
        setOverScrollMode(OVER_SCROLL_NEVER);
        switch (layout_mode) {
            case BasicRecyclerView.HORIZONTAL:
                manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                break;
            case BasicRecyclerView.VERTICAL:
                manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                break;
            case BasicRecyclerView.INVALID_OFFSET:
                manager = new LinearLayoutManager(context, LinearLayoutManager.INVALID_OFFSET, false);
                break;
            case BasicRecyclerView.GRID:
                manager = new GridLayoutManager(context, column_num);
                break;
            case BasicRecyclerView.NONE:
                break;
        }
        //排版方式
        setLayoutManager(manager);
        //添加间隔线
        if (has_row_divider) {
            HorizontalDividerItemDecoration decoration = new HorizontalDividerItemDecoration.Builder(context)
                    .color(divider_color)
                    .size(divider_height)
                    .margin(divider_marginLeft, divider_marginRight)
                    .build();
            addItemDecoration(decoration);
        }
        if (has_rank_divider) {
            VerticalDividerItemDecoration decoration = new VerticalDividerItemDecoration.Builder(context)
                    .color(divider_color)
                    .size(divider_width)
                    .margin(divider_marginTop, divider_marginBottom)
                    .build();
            addItemDecoration(decoration);
        }
        setHasFixedSize(true);
    }

    /**
     * 跳转到指定行
     *
     * @param position 行号
     * @param offset   偏移量
     */
    public void skipPosition(int position, int offset) {
        if (manager instanceof LinearLayoutManager)
            ((LinearLayoutManager) manager).scrollToPositionWithOffset(position, offset);
        else if (manager instanceof GridLayoutManager)
            throw new RuntimeException("RecyclerView在Grid模式无法实现行跳转");
    }

    /**
     * 跳转到指定行
     *
     * @param position 行号
     */
    public void skipPosition(int position) {
        skipPosition(position, 0);
    }

    /**
     * 跳转下一行
     *
     * @param offset 偏移量
     */
    public void skipNext(int offset) {
        View topView = manager.getChildAt(0);
        int currentPosition = manager.getPosition(topView);
        int next = currentPosition + 1;
        int size = getBasicAdapter().getItemCount();
        if (next < size) skipPosition(next, offset);
    }

    /**
     * 跳转下一行
     */
    public void skipNext() {
        skipNext(0);
    }

    /**
     * 跳转上一行
     *
     * @param offset 偏移量
     */
    public void skipPrevious(int offset) {
        View topView = manager.getChildAt(0);
        int currentPosition = manager.getPosition(topView);
        int PreviousPosition = currentPosition - 1;
        if (PreviousPosition >= 0) skipPosition(PreviousPosition, offset);
    }

    /**
     * 跳转上一行
     */
    public void skipPrevious() {
        skipPrevious(0);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof BasicAdapter) {
            BasicAdapter basicAdapter = (BasicAdapter) adapter;
            basicAdapter.setStartAnimation(isStartAnimator);
            basicAdapter.setDuration(mDuration);
            basicAdapter.setFirstOnly(isFirstOnly);
            super.setAdapter(basicAdapter);
        }
        super.setAdapter(adapter);
    }

    /**
     * 获得BasicAdapter实例
     *
     * @return BasicAdapter实例
     */
    private BasicAdapter getBasicAdapter() {
        return (BasicAdapter) getAdapter();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        getBasicAdapter().setOnItemClickListener(onItemClickListener);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        getBasicAdapter().setOnItemLongClickListener(onItemLongClickListener);
    }
}
