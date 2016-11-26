package com.sunnybear.rxandroid.view;

import android.widget.EditText;
import android.widget.TextView;

import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
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

    public RxAndroidViewBinder(Presenter presenter) {
        super(presenter);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rx_android;
    }

    @Override
    public void onViewCreatedFinish() {

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