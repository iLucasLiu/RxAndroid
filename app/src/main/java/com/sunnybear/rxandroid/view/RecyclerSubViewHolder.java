package com.sunnybear.rxandroid.view;

import android.view.View;
import android.widget.TextView;

import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.recycler.adapter.BasicViewHolder;
import com.sunnybear.library.widget.recycler.ItemViewLayoutId;
import com.sunnybear.rxandroid.R;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/11/27.
 */
@ItemViewLayoutId(R.layout.item_sub_text)
public class RecyclerSubViewHolder extends BasicViewHolder<String> {
    @Bind(R.id.tv_sub_content)
    TextView mTvSubContent;

    public RecyclerSubViewHolder(ViewBinder viewBinder, View itemView) {
        super(viewBinder, itemView);
    }

    @Override
    public void onBindItem(String s, int position) {
        mTvSubContent.setText(s);
        mTvSubContent.setOnClickListener(v -> Logger.d("点击第" + (position + 1) + "项"));
    }
}
