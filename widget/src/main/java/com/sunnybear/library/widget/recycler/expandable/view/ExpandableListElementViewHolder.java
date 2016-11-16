package com.sunnybear.library.widget.recycler.expandable.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import com.sunnybear.library.widget.recycler.expandable.element.ExpandableListElement;
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem;

import butterknife.ButterKnife;

/**
 * 扩展中间节点ViewHolder
 * Created by chenkai.gu on 2016/11/1.
 */
public abstract class ExpandableListElementViewHolder<Item extends ExpandableListElement> extends AbstractExpandableAdapterItem {
    protected Context mContext;

    public ExpandableListElementViewHolder(Context context) {
        mContext = context;
    }

    @Override
    public void onExpansionToggled(boolean expanded) {
        float start, target;
        if (expanded) {
            start = 0f;
            target = 90f;
        } else {
            start = 90f;
            target = 0f;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(getArrowView(), View.ROTATION, start, target);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    @Override
    public final void onBindViews(View root) {
        ButterKnife.bind(this, root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doExpandOrUnexpand();
            }
        });
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public final void onUpdateViews(Object model, int position) {
        super.onUpdateViews(model, position);
        onSetViews();
        onExpansionToggled(getExpandableListItem().isExpanded());
        Item element = (Item) model;
        onBindItem(element, position);
    }

    /**
     * 设置指针View(可转动的三角符号)
     */
    public abstract View getArrowView();

    /**
     * 向itemView上绑定数据
     *
     * @param element  item数据
     * @param position 该条数据的位置
     */
    public abstract void onBindItem(Item element, int position);
}
