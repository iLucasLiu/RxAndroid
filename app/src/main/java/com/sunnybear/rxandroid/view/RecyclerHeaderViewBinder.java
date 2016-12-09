package com.sunnybear.rxandroid.view;

import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.widget.recycler.BasicRecyclerView;
import com.sunnybear.library.widget.recycler.adapter.BasicAdapter;
import com.sunnybear.library.widget.recycler.adapter.BasicViewHolder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.model.Position;
import com.sunnybear.rxandroid.presenter.RecyclerHeaderActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/12/9.
 */
public class RecyclerHeaderViewBinder extends ViewBinder<RecyclerHeaderActivity> {
    @Bind(R.id.rv_content)
    BasicRecyclerView mRvContent;

    private BasicAdapter<Position, RecyclerViewHolder> mAdapter;

    public RecyclerHeaderViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recycler_header;
    }

    @Override
    public void onViewCreatedFinish() {
        mAdapter = new BasicAdapter<Position, RecyclerViewHolder>(mContext, null) {
            @Override
            public RecyclerViewHolder getViewHolder(View itemView, int viewType) {
                return new RecyclerViewHolder(mViewBinder, itemView);
            }

            @Override
            public Class<? extends BasicViewHolder> getViewHolderClass(int viewType) {
                return RecyclerViewHolder.class;
            }
        };
        mRvContent.setAdapter(mAdapter);
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "content":
                this.<List<Position>>receiver(tag)
                        .doOnNext(positions -> mAdapter.addAll(positions))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
        }
    }
}