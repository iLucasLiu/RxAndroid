package com.sunnybear.rxandroid.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.recycler.QuickRecyclerView;
import com.sunnybear.library.widget.recycler.adapter.QuickAdapter;
import com.sunnybear.library.widget.recycler.adapter.QuickViewHolder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.model.Position;
import com.sunnybear.rxandroid.presenter.RecyclerActivity;
import com.sunnybear.rxandroid.presenter.RecyclerFragment;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerViewBinder extends ViewBinder<Presenter> implements View.OnClickListener {
    @Bind(R.id.rv_content)
    QuickRecyclerView mRvContent;

    private QuickAdapter<Position, RecyclerViewHolder> mAdapter;

    public RecyclerViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void onBindView(Bundle args) {
        Logger.e("启动fragment:" + args.getInt("position"));
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        mAdapter = new QuickAdapter<Position, RecyclerViewHolder>(mContext, null) {
            @Override
            public RecyclerViewHolder getViewHolder(View itemView, int viewType) {
                return new RecyclerViewHolder(RecyclerViewBinder.this, itemView);
            }

            @Override
            public Class<? extends QuickViewHolder> getViewHolderClass(int viewType) {
                return RecyclerViewHolder.class;
            }
        };
        mRvContent.setAdapter(mAdapter);
//        OverScrollDecoratorHelper.setUpOverScroll(mRvContent, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
//        mRvContent.skipPosition(10);
    }

    @Override
    public void receiveObservableFromPresenter(String tag) {
        switch (tag) {
            case "content":
                if (mPresenter instanceof RecyclerActivity)
                    this.<List<Position>>receiver(tag)
                            .doOnNext(positions -> mAdapter.addAll(positions))
                            .compose(((RecyclerActivity) mPresenter).bindUntilEvent(ActivityEvent.STOP)).subscribe();
                else if (mPresenter instanceof RecyclerFragment)
                    this.<List<Position>>receiver(tag)
                            .doOnNext(positions -> mAdapter.addAll(positions))
                            .compose(((RecyclerFragment) mPresenter).bindUntilEvent(FragmentEvent.STOP)).subscribe();
                break;
        }
    }

    @OnClick({R.id.btn_previous, R.id.btn_next})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                mRvContent.skipPrevious();
                break;
            case R.id.btn_next:
                mRvContent.skipNext();
                break;
        }
    }
}
