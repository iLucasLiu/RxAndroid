package com.sunnybear.rxandroid.view;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sunnybear.library.basic.bus.RxBusSubscriber;
import com.sunnybear.library.basic.bus.RxEvent;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.util.DateUtils;
import com.sunnybear.library.util.DynamicLoaderProvider;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.library.util.log.LogOutput;
import com.sunnybear.rxandroid.R;
import com.dynamic.IDynamic;
import com.sunnybear.rxandroid.presenter.MainActivity;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class MainViewBinder extends ViewBinder<MainActivity> implements View.OnClickListener {
    @Bind(R.id.tv_content)
    TextView mTvContent;

    public MainViewBinder(Presenter presenter) {
        super(presenter);
        LogOutput.initialize(this.getClass());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onViewCreatedFinish() {
        RxEvent.subscriber(RxEvent.getEvent()
                .subscribe(new RxBusSubscriber<String>() {
                    @Override
                    protected void onEvent(String tag, String s) {
                        mTvContent.setText(s);
                    }
                }));
    }

    @Override
    public void receiveObservable(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag)
                        .doOnNext(s -> Logger.d("View接收到的字符串是:" + s))
                        .doOnComplete(() -> Logger.i("View字符串接收完成"))
                        .compose(mPresenter.bindUntilEvent(ActivityEvent.STOP))
                        .subscribe();
                break;
            case "number":
                this.<Integer>receiveArray(tag)
                        .filter(integer -> integer > 4)
                        .doOnNext(integer -> Log.d("RxAndroid", "View接收到的数字是:" + integer.toString()))
                        .doOnComplete(() -> Logger.i("View数字接收完成"))
                        .compose(mPresenter.bindToLifecycle()).subscribe();
                break;
            case "result":
                this.<String>receive(tag)
                        .doOnNext(s -> mTvContent.setText(s))
                        .compose(mPresenter.bindToLifecycle()).subscribe();
                break;
        }
    }

    @OnClick({
            R.id.btn_request, R.id.btn_download, R.id.btn_send,
            R.id.btn_start, R.id.btn_watermark, R.id.btn_dynamic,
            R.id.btn_log, R.id.btn_db})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request:
                sendToPresenter("request", "103", "json", "379020", "西湖", "600");
                break;
            case R.id.btn_download:
                sendToPresenter("download", Flowable.empty());
                break;
            case R.id.btn_send:
                sendToPresenter("string", "Hello RxJava");
                sendToPresenter("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
                break;
            case R.id.btn_start:
                sendToPresenter("start", Flowable.empty());
                break;
            case R.id.btn_watermark:
                ImageUtils.addWatermark(
                        SDCardUtils.getSDCardPath() + "/IMG_20161027_124618.jpg",
                        DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), 10,
                        ImageUtils.WatermarkLocation.BOTTOM_RIGHT,
                        mPresenter.bindUntilEvent(ActivityEvent.STOP)
                );
                break;
            case R.id.btn_dynamic:
                IDynamic dynamic = DynamicLoaderProvider.dynamicLoader(mContext,
                        new File(SDCardUtils.getSDCardPath() + "/fixed_dex.jar"), "com.dynamic.DynamicImpl");
                mTvContent.setText(dynamic.call("24K纯傻"));
                break;
            case R.id.btn_log:
                LogOutput.getLogger().debug("这是一条测试日志");
                LogOutput.getLogger().info("这是一条日志");
                LogOutput.getLogger().error("这是一条错误测试日志");
                break;
            case R.id.btn_db:
                sendToPresenter("db", Flowable.empty());
                break;
        }
    }
}
