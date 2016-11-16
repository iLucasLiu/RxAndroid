package com.sunnybear.library.widget.recycler.expandable.view;

import android.content.Context;
import android.view.View;

import com.sunnybear.library.widget.recycler.expandable.element.ExpandableElement;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem;

import butterknife.ButterKnife;

/**
 * 最终节点元素ViewHolder
 * Created by chenkai.gu on 2016/11/1.
 */
public abstract class ExpandableElementViewHolder<Item extends ExpandableElement> extends AbstractAdapterItem {
    protected Context mContext;
    protected View mItemView;

    public ExpandableElementViewHolder(Context context) {
        mContext = context;
    }

    @Override
    public void onBindViews(View root) {
        ButterKnife.bind(this, root);
        mItemView = root;
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object model, int position) {
        Item element = (Item) model;
        onBindItem(element, position);
    }

    /**
     * 向itemView上绑定数据
     *
     * @param element  item数据
     * @param position 该条数据的位置
     */
    public abstract void onBindItem(Item element, int position);
}
