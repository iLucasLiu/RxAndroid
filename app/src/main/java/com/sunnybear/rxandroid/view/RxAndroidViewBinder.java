package com.sunnybear.rxandroid.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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
    @Bind(R.id.user_name)
    TextInputEditText mUserName;
    @Bind(R.id.user_password)
    TextInputEditText mUserPassword;

    public RxAndroidViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rx_android;
    }

    @Override
    public void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {
        super.onViewCreatedFinish(savedInstanceState);
        mUserName.addTextChangedListener(new TimerTaskTextWatch(1, mPresenter.bindUntilEvent(ActivityEvent.STOP)) {
            @Override
            protected void onTriggerTask(String input) {
                Logger.i("mEtEdit保存:" + input);
            }
        });
        mUserPassword.addTextChangedListener(new TimerTaskTextWatch(1, mPresenter.bindUntilEvent(ActivityEvent.STOP)) {
            @Override
            protected void onTriggerTask(String input) {
                Logger.i("mEtEdit1保存:" + input);
            }
        });
    }

    @Override
    public void receiveObservableFromPresenter(String tag) {
        switch (filterTag(tag)) {
            case "mobile":
                this.<String>receiver(tag)
                        .map(s -> s.substring(0, s.length() - 1))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP))
                        .subscribe(s -> mTvPerson.setText(s));
                break;
        }
    }
}