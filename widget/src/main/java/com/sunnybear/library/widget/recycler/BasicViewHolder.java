package com.sunnybear.library.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunnybear.library.basic.view.ViewBinder;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * 重新封装ViewHolder
 * Created by guchenkai on 2015/11/9.
 */
public abstract class BasicViewHolder<Item extends Serializable> extends RecyclerView.ViewHolder {
    protected ViewBinder mViewBinder;

    public BasicViewHolder(ViewBinder viewBinder, View itemView) {
        super(itemView);
        mViewBinder = viewBinder;
        ButterKnife.bind(this, itemView);
    }

    public BasicViewHolder(View itemView) {
        this(null, itemView);
    }

    /**
     * 向itemView上绑定数据
     *
     * @param item     item数据
     * @param position 该条数据的位置
     */
    public abstract void onBindItem(Item item, int position);
}
