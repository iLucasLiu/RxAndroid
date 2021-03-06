package com.sunnybear.rxandroid.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.widget.recycler.QuickRecyclerView;
import com.sunnybear.library.widget.recycler.adapter.QuickAdapter;
import com.sunnybear.library.widget.recycler.adapter.QuickViewHolder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.model.Position;
import com.sunnybear.rxandroid.presenter.RecyclerHeaderActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class RecyclerHeaderViewBinder extends ViewBinder<RecyclerHeaderActivity> implements View.OnClickListener {
    @Bind(R.id.rv_content)
    QuickRecyclerView mRvContent;
    @Bind(R.id.appbar)
    AppBarLayout mAppbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;

    private QuickAdapter<Position, RecyclerViewHolder> mAdapter;

    public RecyclerHeaderViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recycler_header;
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        mAdapter = new QuickAdapter<Position, RecyclerViewHolder>(mContext, null) {
            @Override
            public RecyclerViewHolder getViewHolder(View itemView, int viewType) {
                return new RecyclerViewHolder(mViewBinder, itemView);
            }

            @Override
            public Class<? extends QuickViewHolder> getViewHolderClass(int viewType) {
                return RecyclerViewHolder.class;
            }
        };
        mRvContent.setAdapter(mAdapter);
    }

    @Override
    public void addListener() {
        mAppbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) ((double) Math.abs(verticalOffset) / (double) maxScroll);
            mFab.setAlpha(percentage);
        });
    }

    @Override
    public void receiveObservableFromPresenter(String tag) {
        switch (tag) {
            case "content":
                this.<List<Position>>receiver(tag)
                        .doOnNext(positions -> mAdapter.addAll(positions))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
        }
    }

    @OnClick(R.id.fab)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
//                mAppbar.
//                mRvContent.skipPosition(0);
                break;
        }
    }
}