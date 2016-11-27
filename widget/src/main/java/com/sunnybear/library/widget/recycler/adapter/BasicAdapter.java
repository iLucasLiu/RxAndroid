package com.sunnybear.library.widget.recycler.adapter;

import android.animation.Animator;
import android.content.Context;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.recycler.BasicViewHolder;
import com.sunnybear.library.widget.recycler.ItemViewLayoutId;
import com.sunnybear.library.widget.recycler.animators.IAnimation;
import com.sunnybear.library.widget.recycler.animators.ViewHelper;
import com.sunnybear.library.widget.recycler.listener.OnItemClickListener;
import com.sunnybear.library.widget.recycler.listener.OnItemLongClickListener;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 重新封装Adapter
 * Created by guchenkai on 2015/11/9.
 */
public abstract class BasicAdapter<Item extends Serializable, VH extends BasicViewHolder> extends RecyclerView.Adapter<VH> {
    private Context mContext;
    private View mItemView;
    private List<Item> mItems;
    private OnItemClickListener<Item> mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    private boolean isStartAnimation = false;//是否开启ItemView动画
    private boolean isFirstOnly = false;
    private int mDuration;
    private Interpolator mInterpolator;
    private int mLastPosition = -1;
    private IAnimation mIAnimation;
    /*内存缓存,缓存中缓存position和Item的弱引用*/
    private LruCache<Integer, WeakReference<Item>> mMemoryCache;
    private Item currentItem;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public BasicAdapter(Context context, List<Item> items) {
        mContext = context;
        this.mItems = items != null ? items : new ArrayList<>();
        mInterpolator = new LinearInterpolator();
        /*初始化内存缓存*/
        mMemoryCache = getMemoryCache();
    }

    /**
     * 创建内存缓存
     */
    private LruCache<Integer, WeakReference<Item>> getMemoryCache() {
        return new LruCache<Integer, WeakReference<Item>>((int) (Runtime.getRuntime().maxMemory() / 6)) {
            /*当缓存大于我们设定的最大值时，会调用这个方法，我们可以用来做内存释放操作*/
            @Override
            protected void entryRemoved(boolean evicted, Integer key, WeakReference<Item> oldValue, WeakReference<Item> newValue) {
                if (evicted && oldValue != null) oldValue.clear();
            }
        };
    }

    /**
     * 获得全部对象
     *
     * @return
     */
    public List<Item> getItems() {
        return mItems;
    }

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

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<? extends BasicViewHolder> viewHolderClass = getViewHolderClass(viewType);
        ItemViewLayoutId layoutId = viewHolderClass.getAnnotation(ItemViewLayoutId.class);
        if (layoutId != null) {
            mItemView = LayoutInflater.from(mContext).inflate(layoutId.value(), parent, false);
            return getViewHolder(mItemView, viewType);
        } else {
            Logger.e("@ItemViewLayoutId设置错误");
            return null;
        }
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        currentItem = getItemFromMemoryCache(position);
        if (currentItem == null) currentItem = getItem(position);
        holder.onBindItem(currentItem, position);
        addItemToMemoryCache(position, currentItem);
        final View itemView = holder.itemView;
        if (mOnItemClickListener != null)
            itemView.setOnClickListener(v -> {
                Flowable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                        .doOnComplete(() -> mOnItemClickListener.onItemClick(currentItem, position))
                        .subscribe();
            });
        if (mOnItemLongClickListener != null)
            itemView.setOnLongClickListener(v -> {
                Logger.d("长按事件");
                mOnItemLongClickListener.onItemLongClick(currentItem, position);
                return true;
            });
        if (isStartAnimation)
            setAnimator(holder.itemView, position);
    }

    /**
     * 添加Item到内存缓存
     *
     * @param position 下标
     * @param item     Item实体
     */
    private void addItemToMemoryCache(int position, Item item) {
        if (getItemFromMemoryCache(position) == null && item != null)
            mMemoryCache.put(position, new WeakReference<>(item));
    }

    /**
     * 从内存缓存中获取Item
     *
     * @param position 下标
     * @return Item实体
     */
    private Item getItemFromMemoryCache(int position) {
        WeakReference<Item> weakReference = mMemoryCache.get(position);
        if (weakReference != null)
            return weakReference.get();
        return null;
    }

    /**
     * 添加
     *
     * @param item item
     */
    public void add(Item item) {
        mItems.add(item);
        notifyItemInserted(mItems.size() - 1);
    }

    /**
     * 添加
     *
     * @param position 下标
     * @param item     item
     */
    public void add(int position, Item item) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    /**
     * 添加全部
     *
     * @param items items
     */
    public void addAll(List<Item> items) {
        int indexStart = mItems.size();
        mItems.addAll(items);
        notifyItemRangeInserted(indexStart, items.size());
    }

    /**
     * 替换
     *
     * @param oldItem 老item
     * @param newItem 新item
     */
    public void replace(Item oldItem, Item newItem) {
        int index = mItems.indexOf(oldItem);
        replace(index, newItem);
    }

    /**
     * 替换
     *
     * @param index 下标
     * @param item  item
     */
    public void replace(int index, Item item) {
        if (mMemoryCache != null) mMemoryCache.remove(index);
        mItems.set(index, item);
        notifyItemChanged(index);
    }

    /**
     * 替换全部
     *
     * @param items items
     */
    public void replaceAll(List<Item> items) {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) mMemoryCache.evictAll();
            mMemoryCache = getMemoryCache();
        }
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 删除
     *
     * @param item item
     */
    public void delete(Item item) {
        int index = mItems.indexOf(item);
        delete(index);
    }

    /**
     * 删除
     *
     * @param index item下标
     */
    public void delete(int index) {
        mItems.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * 清除
     */
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * 刷新
     */
    public void refresh() {
        notifyDataSetChanged();
    }

    /**
     * 是否包含Item
     *
     * @param item item
     */
    public boolean contains(Item item) {
        return mItems.contains(item);
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

    /**
     * 是否开启动画
     *
     * @param startAnimation 是否开启动画
     */
    public void setStartAnimation(boolean startAnimation) {
        isStartAnimation = startAnimation;
    }

    /**
     * 动画持续时间
     *
     * @param duration 持续时间,单位ms
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 设置动画插入器
     *
     * @param interpolator 动画插入器
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    /**
     * 设置动画开始行的position
     *
     * @param start 开始行的position
     */
    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    /**
     * 是否只执行1次
     *
     * @param firstOnly 是否只执行1次
     */
    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
    }

    /**
     * 设置ViewHolder
     *
     * @param itemView item布局
     * @param viewType view类型
     * @return ViewHolder
     */
    public abstract VH getViewHolder(View itemView, int viewType);

    /**
     * 设置ViewHolder的类型
     *
     * @param viewType 视图种类
     * @return
     */
    public abstract Class<? extends BasicViewHolder> getViewHolderClass(int viewType);
}
