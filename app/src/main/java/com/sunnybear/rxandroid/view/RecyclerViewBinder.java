package com.sunnybear.rxandroid.view;

import android.view.View;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.widget.recycler.BasicRecyclerView;
import com.sunnybear.library.widget.recycler.BasicViewHolder;
import com.sunnybear.library.widget.recycler.adapter.BasicAdapter;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.model.Position;
import com.sunnybear.rxandroid.presenter.RecyclerActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerViewBinder extends ViewBinder<RecyclerActivity> implements View.OnClickListener {
    @Bind(R.id.rv_content)
    BasicRecyclerView mRvContent;

    private BasicAdapter<Position, RecyclerViewHolder> mAdapter;

    public RecyclerViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void onViewCreatedFinish() {
        mAdapter = new BasicAdapter<Position, RecyclerViewHolder>(mContext, null) {
            @Override
            public RecyclerViewHolder getViewHolder(View itemView, int viewType) {
                return new RecyclerViewHolder(RecyclerViewBinder.this, itemView);
            }

            @Override
            public Class<? extends BasicViewHolder> getViewHolderClass(int viewType) {
                return RecyclerViewHolder.class;
            }
        };
        mRvContent.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRvContent, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
//        mRvContent.skipPosition(10);
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "content":
                this.<List<Position>>receive(tag)
                        .doOnNext(positions -> mAdapter.addAll(positions))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP)).subscribe();
//                this.<List<String>>receive(tag)
//                        .doOnNext(strings -> mAdapter.addAll(strings))
//                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP)).subscribe();
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
