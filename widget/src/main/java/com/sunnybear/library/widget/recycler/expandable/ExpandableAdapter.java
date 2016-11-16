package com.sunnybear.library.widget.recycler.expandable;

import android.support.annotation.NonNull;

import com.sunnybear.library.widget.recycler.expandable.element.Element;
import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 可扩展的Adapter
 * Created by chenkai.gu on 2016/11/1.
 */
public abstract class ExpandableAdapter<Item extends Element> extends BaseExpandableAdapter implements BaseExpandableAdapter.ExpandCollapseListener {
    private OnExpandCollapseListener mOnExpandCollapseListener;

    public void setOnExpandCollapseListener(OnExpandCollapseListener onExpandCollapseListener) {
        mOnExpandCollapseListener = onExpandCollapseListener;
    }

    protected ExpandableAdapter(List<Item> elements) {
        super(elements != null ? elements : new ArrayList());
        setExpandCollapseListener(this);
    }

    @NonNull
    @Override
    public AbstractAdapterItem<Object> getItemView(Object type) {
        int itemType = (int) type;
        return getItemView(itemType);
    }

    @Override
    public final Object getItemViewType(Object t) {
        Element item = (Element) t;
        return getItemViewType(item);
    }

    /**
     * 设置ItemView的ViewHolder类型
     *
     * @param item item
     */
    public abstract int getItemViewType(Element item);

    /**
     * 设置ItemView类型
     *
     * @param type ItemView类型
     */
    public abstract AbstractAdapterItem getItemView(int type);

    @Override
    public void onListItemExpanded(int position) {
        if (mOnExpandCollapseListener != null)
            mOnExpandCollapseListener.onListItemExpanded(position);
    }

    @Override
    public void onListItemCollapsed(int position) {
        if (mOnExpandCollapseListener != null)
            mOnExpandCollapseListener.onListItemCollapsed(position);
    }

    /**
     * List元素展开闭合监听
     * Created by chenkai.gu on 2016/11/1.
     */
    public interface OnExpandCollapseListener {
        /**
         * List元素展开监听
         *
         * @param position 元素位置
         */
        void onListItemExpanded(int position);

        /**
         * List元素闭合监听
         *
         * @param position 元素位置
         */
        void onListItemCollapsed(int position);
    }
}
