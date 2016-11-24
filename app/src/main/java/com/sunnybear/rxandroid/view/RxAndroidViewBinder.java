package com.sunnybear.rxandroid.view;

import android.content.Context;
import android.widget.TextView;

import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.rxandroid.R;
import com.sunnybear.rxandroid.presenter.RxAndroidActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import butterknife.Bind;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by chenkai.gu on 2016/11/23.
 */
public class RxAndroidViewBinder extends ViewBinder<RxAndroidActivity> {
    @Bind(R.id.tv_person)
    TextView mTvPerson;

    public RxAndroidViewBinder(Context context) {
        super(context);
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
                this.<String>receive(tag, ActivityEvent.STOP)
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {
                                return s.substring(0, s.length() - 1);
                            }
                        })
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {
                                mTvPerson.setText(s);
                            }
                        });
                break;
        }
    }
}