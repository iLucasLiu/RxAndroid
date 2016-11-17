package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.view.View;

import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.widget.recycler.BasicRecyclerView;
import com.sunnybear.library.widget.recycler.adapter.BasicAdapter;
import com.sunnybear.library.widget.recycler.adapter.BasicViewHolder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.RecyclerActivity;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.List;

import butterknife.Bind;
import rx.functions.Action1;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class RecyclerViewBinder extends ViewBinder<RecyclerActivity> {
    @Bind(R.id.rv_content)
    BasicRecyclerView mRvContent;

    private BasicAdapter<String, RecyclerViewHolder> mAdapter;

    public RecyclerViewBinder(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_recycler;
    }

    @Override
    public void onViewCreatedFinish() {
        mAdapter = new BasicAdapter<String, RecyclerViewHolder>(mContext, null) {
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
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "content":
                this.<List<String>>receive(tag, ActivityEvent.STOP)
                        .doOnNext(new Action1<List<String>>() {
                            @Override
                            public void call(List<String> strings) {
                                mAdapter.addAll(strings);
                            }
                        }).subscribe();
                break;
        }
    }
}
