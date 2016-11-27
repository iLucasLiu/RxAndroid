package com.sunnybear.rxandroid.view;

import android.view.View;
import android.widget.TextView;

import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.recycler.BasicRecyclerView;
import com.sunnybear.library.widget.recycler.BasicViewHolder;
import com.sunnybear.library.widget.recycler.ItemViewLayoutId;
import com.sunnybear.library.widget.recycler.adapter.BasicAdapter;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.model.Position;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
@ItemViewLayoutId(R.layout.item_text)
public class RecyclerViewHolder extends BasicViewHolder<Position> {
    @Bind(R.id.tv_content)
    TextView mTvContent;
    @Bind(R.id.rv_position)
    BasicRecyclerView mRvPosition;
    private BasicAdapter<String, RecyclerSubViewHolder> mAdapter;

    public RecyclerViewHolder(ViewBinder viewBinder, View itemView) {
        super(viewBinder, itemView);
    }

    @Override
    public void onBindItem(Position p, final int position) {
        mTvContent.setText(p.getContent());
        mTvContent.setOnClickListener(v -> {
            Logger.i("点击第" + (position + 1) + "项内容");
//            mViewBinder.sendToPresenter("click", position + 1);
//            RxEvent.post("RxBus", "Hello RxBus");
        });
        mAdapter = new BasicAdapter<String, RecyclerSubViewHolder>(mViewBinder.getContext(), p.getPositions()) {
            @Override
            public RecyclerSubViewHolder getViewHolder(View itemView, int viewType) {
                return new RecyclerSubViewHolder(mViewBinder, itemView);
            }

            @Override
            public Class<? extends BasicViewHolder> getViewHolderClass(int viewType) {
                return RecyclerSubViewHolder.class;
            }
        };
        mRvPosition.setAdapter(mAdapter);
    }
}
