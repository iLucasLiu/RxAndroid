package com.sunnybear.rxandroid.view;

import android.view.View;
import android.widget.TextView;

import com.sunnybear.library.basic.bus.RxEvent;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.widget.recycler.ItemViewLayoutId;
import com.sunnybear.library.widget.recycler.adapter.BasicViewHolder;
import com.sunnybear.rxandroid.R;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
@ItemViewLayoutId(R.layout.item_text)
public class RecyclerViewHolder extends BasicViewHolder<String> {

    @Bind(R.id.tv_content)
    TextView mTvContent;

    public RecyclerViewHolder(ViewBinder viewBinder, View itemView) {
        super(viewBinder, itemView);
    }

    @Override
    public void onBindItem(String s, final int position) {
        mTvContent.setText(s);
        mTvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewBinder.sendToPresenter("click", position + 1);
                RxEvent.post("RxBus", "Hello RxBus");
                RxEvent.post("RxJava", "Hello RxJava");
            }
        });
    }
}
