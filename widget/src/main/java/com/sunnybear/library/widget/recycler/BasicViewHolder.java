package com.sunnybear.library.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunnybear.library.basic.view.ViewBinder;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;

/**
 * 重新封装ViewHolder
 * Created by guchenkai on 2015/11/9.
 */
public abstract class BasicViewHolder<Item extends Serializable> extends RecyclerView.ViewHolder {
    protected ViewBinder mViewBinder;
    private Map<String, Object> mTagMap;

    public BasicViewHolder(ViewBinder viewBinder, View itemView) {
        super(itemView);
        mViewBinder = viewBinder;
        mTagMap = new ConcurrentHashMap<>();
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

    /**
     * 绑定tag
     *
     * @param view     目标View
     * @param position 序号
     */
    public void bindingTag(View view, int position, Object value) {
        mTagMap.put(view.getId() + "_" + position, value);
    }

    /**
     * 是否已经绑定tag
     *
     * @param position 序号
     */
    private boolean hasBindingTag(View view, int position) {
        return mTagMap.containsKey(view.getId() + "_" + position);
    }

    /**
     * 获取绑定tag中的值
     *
     * @param view     目标View
     * @param position 序号
     * @param defValue 默认值
     */
    public <T> T getTagValue(View view, int position, T defValue) {
        if (hasBindingTag(view, position))
            return (T) mTagMap.get(view.getId() + "_" + position);
        return defValue;
    }
}
