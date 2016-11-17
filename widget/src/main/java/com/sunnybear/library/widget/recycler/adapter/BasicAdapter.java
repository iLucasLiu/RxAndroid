package com.sunnybear.library.widget.recycler.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.PhoneUtil;
import com.sunnybear.library.widget.recycler.ItemViewLayoutId;
import com.sunnybear.library.widget.recycler.animators.IAnimation;
import com.sunnybear.library.widget.recycler.animators.ViewHelper;
import com.sunnybear.library.widget.recycler.listener.OnItemClickListener;
import com.sunnybear.library.widget.recycler.listener.OnItemLongClickListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 重新封装Adapter
 * Created by guchenkai on 2015/11/9.
 */
public abstract class BasicAdapter<Item extends Serializable, VH extends BasicViewHolder> extends RecyclerView.Adapter<VH> {
    protected Context mContext;
    private View mItemView;
    protected List<Item> mItems;
    private OnItemClickListener<Item> mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private int mProcessDrawable;

    private boolean isStartAnimation = false;//是否开启ItemView动画
    private boolean isFirstOnly = false;
    private int mDuration;
    private Interpolator mInterpolator;
    private int mLastPosition = -1;
    private IAnimation mIAnimation;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public void setProcessDrawable(int processDrawable) {
        mProcessDrawable = processDrawable;
    }

    public BasicAdapter(Context context, List<Item> items) {
        mContext = context;
        this.mItems = items != null ? items : new ArrayList<Item>();
        mInterpolator = new LinearInterpolator();
    }

    public void setStartAnimation(boolean startAnimation) {
        isStartAnimation = startAnimation;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }

    /**
     * 获得全部对象
     *
     * @return
     */
    public List<Item> getItems() {
        return mItems;
    }

    /**
     * 设置ViewHolder
     *
     * @param context  上下文环境
     * @param itemView item布局
     * @param viewType view类型
     * @return ViewHolder
     */
    public abstract VH getViewHolder(Context context, View itemView, int viewType);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * 获得item
     *
     * @param position 标号
     * @return item
     */
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public final int getItemViewType(int position) {
        Item item = getItem(position);
        return getItemViewType(item, position);
    }

    /**
     * 设置ItemType
     *
     * @param item     每一行绑定的数据
     * @param position 标号
     */
    public int getItemViewType(Item item, int position) {
        return 0;
    }

    /**
     * 设置ViewHolder的类型
     *
     * @param viewType 视图种类
     * @return
     */
    public abstract Class<? extends BasicViewHolder> getViewHolderClass(int viewType);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends BasicViewHolder> viewHolderClass = getViewHolderClass(viewType);
        ItemViewLayoutId layoutId = viewHolderClass.getAnnotation(ItemViewLayoutId.class);
        if (layoutId != null){
            mItemView = LayoutInflater.from(mContext).inflate(layoutId.value(), parent, false);
            mItemView.setBackgroundResource(mProcessDrawable);
            return getViewHolder(mContext, mItemView, viewType);
        }else {
            Logger.e("@ItemViewLayoutId设置错误");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final Item item = mItems.get(position);
        final View itemView = holder.itemView;
        if (mOnItemClickListener != null)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PhoneUtil.isFastDoubleClick())
                        mOnItemClickListener.onItemClick(item, position);
                    else
                        Logger.e("重复点击");
                }
            });
        if (mOnItemLongClickListener != null)
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Logger.d("长按事件");
                    mOnItemLongClickListener.onItemLongClick(item, position);
                    return true;
                }
            });
        if (isStartAnimation)
            setAnimator(holder.itemView, position);
        holder.onBindItem(item, position);
    }

    /**
     * 设置动画
     *
     * @param itemView itemView
     * @param position position
     */
    private void setAnimator(View itemView, int position) {
        if (!isFirstOnly || position > mLastPosition) {
            for (Animator anim : mIAnimation.getAnimators(itemView)) {
                anim.setDuration(mDuration).start();
                anim.setInterpolator(mInterpolator);
            }
            mLastPosition = position;
        } else {
            ViewHelper.clear(itemView);
        }
    }

    /**
     * 设置itemView动画组
     *
     * @param animation 动画组接口
     */
    public void setAnimations(IAnimation animation) {
        mIAnimation = animation;
    }

    public void add(Item item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    public void add(int position, Item item) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void addAll(List<Item> items) {
        int indexStart = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(indexStart, items.size());
    }

    public void replace(Item oldItem, Item newItem) {
        int index = mItems.indexOf(oldItem);
        replace(index, newItem);
    }

    public void replace(int index, Item item) {
        mItems.set(index, item);
        notifyItemChanged(index);
    }

    public void replaceAll(List<Item> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void delete(Item item) {
        int index = mItems.indexOf(item);
        delete(index);
    }

    public void delete(int index) {
        mItems.remove(index);
        notifyItemRemoved(index);
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public boolean contains(Item item) {
        return mItems.contains(item);
    }
}
