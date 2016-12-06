package com.sunnybear.rxandroid.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunnybear.library.basic.model.BindModel;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.rxandroid.db.entity.User;
import com.sunnybear.rxandroid.model.DownloadModelProcessor;
import com.sunnybear.rxandroid.model.MainModelProcessor;
import com.sunnybear.rxandroid.view.MainViewBinder;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.List;

public class MainActivity extends PresenterActivity<MainViewBinder> {
    @BindModel
    MainModelProcessor mMainModelProcessor;
    @BindModel
    DownloadModelProcessor mDownloadModelProcessor;

    @Override
    protected MainViewBinder getViewBinder(Presenter presenter) {
        return new MainViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);

        send("string", "Hello RxJava");
        send("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
    }

    @Override
    public void receiveObservableFromView(String tag) {
        switch (filterTag(tag)) {
            case "string":
                this.<String>receive(tag)
                        .doOnNext(s -> Logger.d("Presenter接收到的字符串是:" + s))
                        .doOnComplete(() -> Logger.i("Presenter字符串接收完成"))
                        .compose(bindUntilEvent(ActivityEvent.STOP))
                        .subscribe();
                break;
            case "number":
                this.<Integer>receiveArray(tag)
                        .filter(integer -> integer > 4)
                        .doOnNext(integer -> Log.d("RxAndroid", "Presenter接收到的数字是:" + integer.toString()))
                        .doOnComplete(() -> Logger.i("Presenter数字接收完成"))
                        .compose(bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
            case "request":
                this.<String[]>receive(tag)
                        .doOnNext(strings ->
                                mMainModelProcessor.getBaike(strings[0], strings[1], strings[2], strings[3], strings[4])
                        ).compose(bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
            case "download":
                mDownloadModelProcessor.download(
                        "http://10.103.18.196:8089/SFAInterface/appservice/downloadFile.htm?mobileLoginNumber=100",
                        SDCardUtils.getSDCardPath() + "/rxjava/100.zip");
                break;
            case "start":
//                Bitmap bitmap = BitmapFactory.decodeFile(SDCardUtils.getSDCardPath() + "/IMG_20161027_124618.jpg");
//                NativeUtil.compressBitmap(bitmap, 10, SDCardUtils.getSDCardPath() + "/IMG_20161027_124618_compress.jpg", false);
                startActivity(new Intent(mContext, RecyclerActivity.class));
                break;
            case "db":
//                mMainModelProcessor.saveUser();
                List<User> users = mMainModelProcessor.getUsers();
                Logger.d(users.toString());
                break;
        }
    }
}
