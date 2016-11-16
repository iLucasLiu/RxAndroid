package com.sunnybear.library.widget.recycler.expandable.element;

import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem;

/**
 * 扩展中间节点元素
 * Created by chenkai.gu on 2016/11/1.
 */
public abstract class ExpandableListElement implements ExpandableListItem, Element {
    /*是否默认展开*/
    private boolean mExpanded = false;

    @Override
    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        mExpanded = isExpanded;
    }
}
