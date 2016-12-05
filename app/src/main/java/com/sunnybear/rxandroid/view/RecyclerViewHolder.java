package com.sunnybear.rxandroid.view;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.widget.recycler.BasicRecyclerView;
import com.sunnybear.library.widget.recycler.adapter.BasicViewHolder;
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
    @Bind(R.id.cb_sel)
    CheckBox mCbSel;
    private BasicAdapter<String, RecyclerSubViewHolder> mAdapter;

    public RecyclerViewHolder(ViewBinder viewBinder, View itemView) {
        super(viewBinder, itemView);
    }

    @Override
    public void onBindItem(Position p, final int position) {
        mTvContent.setText(p.getContent());
        CheckBoxAttrs attrs = getTagValue(mCbSel, p, new CheckBoxAttrs(false, Color.BLUE));
        mCbSel.setChecked(attrs.isSel());
        mTvContent.setTextColor(attrs.getColor());
        itemView.setOnClickListener(v -> {
            Logger.i("点击第" + (position + 1) + "项内容");
            boolean isChecked = mCbSel.isChecked();
            mCbSel.setChecked(!isChecked);
            if (!isChecked)
                mTvContent.setTextColor(Color.RED);
            else
                mTvContent.setTextColor(Color.BLUE);
            CheckBoxAttrs attrs1 = new CheckBoxAttrs();
            attrs1.setSel(!isChecked);
            attrs1.setColor(mTvContent.getCurrentTextColor());
            bindingTag(mCbSel, p, attrs1);
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

    public class CheckBoxAttrs {
        private boolean isSel;
        private int mColor;

        public CheckBoxAttrs(boolean isSel, int color) {
            this.isSel = isSel;
            mColor = color;
        }

        public CheckBoxAttrs() {
        }

        public boolean isSel() {
            return isSel;
        }

        public void setSel(boolean sel) {
            isSel = sel;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int color) {
            mColor = color;
        }
    }
}
