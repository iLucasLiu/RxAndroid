package com.sunnybear.rxandroid.view;

import android.widget.EditText;
import android.widget.TextView;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.TimerTaskTextWatch;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.RxAndroidActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.Bind;

/**
 * Created by chenkai.gu on 2016/11/23.
 */
public class RxAndroidViewBinder extends ViewBinder<RxAndroidActivity> {
    @Bind(R.id.tv_person)
    TextView mTvPerson;
    @Bind(R.id.et_edit)
    EditText mEtEdit;
    @Bind(R.id.et_edit1)
    EditText mEtEdit1;

    public RxAndroidViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rx_android;
    }

    @Override
    public void onViewCreatedFinish() {
        mEtEdit.addTextChangedListener(new TimerTaskTextWatch(1, mPresenter.bindUntilEvent(ActivityEvent.STOP)) {
            @Override
            protected void onTriggerTask(String input) {
                Logger.i("mEtEdit保存:" + input);
            }
        });
        mEtEdit1.addTextChangedListener(new TimerTaskTextWatch(1, mPresenter.bindUntilEvent(ActivityEvent.STOP)) {
            @Override
            protected void onTriggerTask(String input) {
                Logger.i("mEtEdit1保存:" + input);
            }
        });
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "mobile":
                this.<String>receive(tag)
                        .map(s -> s.substring(0, s.length() - 1))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP))
                        .subscribe(s -> mTvPerson.setText(s));
                break;
        }
    }
}