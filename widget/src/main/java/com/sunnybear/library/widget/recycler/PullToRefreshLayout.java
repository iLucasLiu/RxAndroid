package com.sunnybear.library.widget.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sunnybear.library.util.DensityUtil;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.widget.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 下拉刷新组件
 * Created by guchenkai on 2015/11/25.
 */
public class PullToRefreshLayout extends PtrFrameLayout implements PtrUIHandler, PtrHandler {
    private static final String TAG = PullToRefreshLayout.class.getSimpleName();
    private TextView mRefreshText;//下拉刷新时加载文字
    private View mRefreshView;//下拉刷新时有可以旋转的view
    private RotateAnimation mRotateAnimation;//旋转动画

    private OnRefreshListener mOnRefreshListener;
    private int mRefreshLayoutId;
    private int mRefreshLayoutHeight;

    private String mPrepareText;//准备刷新时的文字
    private String mRecycleText;//可以释放时的文字
    private String mLoadingText;//正在加载时的文字
    private String mCompleteText;//加载完成时的文字

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public PullToRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initStyleable(context, attrs);
        initAnimation();
        initView(context);
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);
        mRefreshLayoutId = array.getResourceId(R.styleable.PullToRefreshLayout_refresh_layout, 0);
        mRefreshLayoutHeight = array.getInt(R.styleable.PullToRefreshLayout_refresh_layout_height, 0);
        mPrepareText = array.getString(R.styleable.PullToRefreshLayout_refresh_prepare_text);
        if (StringUtils.isEmpty(mPrepareText))
            mPrepareText = context.getResources().getString(R.string.pullToRefresh_prepare);
        mRecycleText = array.getString(R.styleable.PullToRefreshLayout_refresh_recycle_text);
        if (StringUtils.isEmpty(mRecycleText))
            mRecycleText = context.getResources().getString(R.string.pullToRefresh_recycle);
        mLoadingText = array.getString(R.styleable.PullToRefreshLayout_refresh_loading_text);
        if (StringUtils.isEmpty(mLoadingText))
            mLoadingText = context.getResources().getString(R.string.pullToRefresh_loading);
        mCompleteText = array.getString(R.styleable.PullToRefreshLayout_refresh_complete_text);
        if (StringUtils.isEmpty(mCompleteText))
            mCompleteText = context.getResources().getString(R.string.pullToRefresh_complete);
        array.recycle();
    }

    /**
     * 初始化旋转动画
     */
    private void initAnimation() {
        mRotateAnimation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    /**
     * 初始化布局
     *
     * @param context context
     */
    private void initView(Context context) {
        View parentView = LayoutInflater.from(context).inflate(mRefreshLayoutId, null);
        parentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , DensityUtil.dp2px(this.getContext(), mRefreshLayoutHeight)));
        mRefreshText = (TextView) parentView.findViewById(R.id.refresh_text);
        mRefreshView = parentView.findViewById(R.id.refresh_view);

        setHeaderView(parentView);
        setPtrHandler(this);
        addPtrUIHandler(this);
    }


    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        if (content instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) content;
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisiblePosition = manager.findFirstCompletelyVisibleItemPosition();
            if (firstVisiblePosition == 0)
                return true;
        } /*else if (content instanceof StickyHeaderLayout) {
            StickyHeaderLayout layout = (StickyHeaderLayout) content;
//            RecyclerView recyclerView = (RecyclerView) ((FrameLayout) layout.getChildAt(3)).getChildAt(0);
            RecyclerView recyclerView = (RecyclerView) layout.getChildAt(3);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int firstVisiblePosition = manager.findFirstCompletelyVisibleItemPosition();
            if (firstVisiblePosition == 0 && layout.isScrollTop())
                return true;
        }*/ else if (content instanceof ScrollView) {
//            FrameLayout layout = (FrameLayout) content;
//            RecyclerView recyclerView = (RecyclerView) layout.getChildAt(0);
//            if (recyclerView == null)
//                throw new RuntimeException("第一个view不是RecyclerView");
//            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//            int firstVisiblePosition = manager.findFirstCompletelyVisibleItemPosition();
//            if (firstVisiblePosition == 0)
            return true;
        } else if (content instanceof FrameLayout) {

            return true;
        } else if (!(content instanceof RecyclerView ||
                content instanceof FrameLayout ||
                content instanceof ScrollView)) {
            throw new RuntimeException("PullToRefreshLayout中只能放RecyclerView或FrameLayout或ScrollView");
        }
        return false;
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        Logger.d(TAG, "onRefreshBegin");
        if (mOnRefreshListener != null)
            mOnRefreshListener.onRefresh();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int currentPos = ptrIndicator.getCurrentPosY();
        switch (status) {
            case PtrFrameLayout.PTR_STATUS_PREPARE://下拉时
                mRefreshText.setText(mPrepareText);
                mRefreshView.setRotation(currentPos);
                if (currentPos >= getHeight())
                    mRefreshText.setText(mRecycleText);
                break;
            case PtrFrameLayout.PTR_STATUS_LOADING://释放加载时
                mRefreshText.setText(mLoadingText);
                mRefreshView.startAnimation(mRotateAnimation);
                break;
            case PtrFrameLayout.PTR_STATUS_COMPLETE://更新完成时
                mRefreshText.setText(mCompleteText);
                mRefreshView.clearAnimation();
                break;
            case PtrFrameLayout.PTR_STATUS_INIT://初始化
                break;
        }
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        Logger.d("onUIReset");
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        Logger.d("onUIRefreshPrepare");
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        Logger.d("onUIRefreshBegin");
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        Logger.d("onUIRefreshComplete");
    }

    /**
     * 下拉刷新回调
     */
    public interface OnRefreshListener {

        void onRefresh();
    }
}
