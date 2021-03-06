package com.sunnybear.rxandroid.presenter;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sunnybear.library.basic.model.BindModel;
import com.sunnybear.library.basic.permission.PermissionActivity;
import com.sunnybear.library.basic.permission.PermissionsChecker;
import com.sunnybear.library.basic.presenter.Presenter;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.util.DateUtils;
import com.sunnybear.library.util.ImageUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;
import com.sunnybear.rxandroid.model.DownloadModelProcessor;
import com.sunnybear.rxandroid.model.MainModelProcessor;
import com.sunnybear.rxandroid.view.MainViewBinder;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Consumer;

public class MainActivity extends PresenterActivity<MainViewBinder> {
    @BindModel
    MainModelProcessor mMainModelProcessor;
    @BindModel
    DownloadModelProcessor mDownloadModelProcessor;

    private PermissionsChecker mPermissionsChecker;
    private static final int PERMISSIONS_REQUEST_CODE = 0; // 请求码
    /*权限检查器*/
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected MainViewBinder getViewBinder(Presenter presenter) {
        return new MainViewBinder(presenter);
    }

    @Override
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {
        super.onViewBindFinish(savedInstanceState);
//        sendToView("string", "Hello RxJava");
//        sendToView("number", 1, 2, 3, 4, 5, 6, 7, 8, 9, 0);
//        mMainModelProcessor = new MainModelProcessor(this);
        mPermissionsChecker = new PermissionsChecker(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //缺少权限时,进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS))
            PermissionActivity.startActivityForResult(this, PERMISSIONS_REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //拒绝时,关闭页面,缺少主要权限,无法运行
        if (requestCode == PERMISSIONS_REQUEST_CODE && resultCode == PermissionActivity.PERMISSIONS_DENIED)
            finish();
    }

    @Override
    public void receiveObservableFromView(String tag) {
        switch (tag) {
            case "string":
                this.<String>receiver(tag)
                        .doOnNext(s -> Logger.d("Presenter接收到的字符串是:" + s))
                        .doOnComplete(() -> Logger.i("Presenter字符串接收完成"))
                        .compose(bindUntilEvent(ActivityEvent.STOP))
                        .subscribe();
                break;
            case "number":
                this.<Integer>receiverArray(tag)
                        .filter(integer -> integer > 4)
                        .doOnNext(integer -> Log.d("RxAndroid", "Presenter接收到的数字是:" + integer.toString()))
                        .doOnComplete(() -> Logger.i("Presenter数字接收完成"))
                        .compose(bindUntilEvent(ActivityEvent.STOP)).subscribe();
                break;
            case "request":
                this.<String[]>receiver(tag)
                        .doOnNext(new Consumer<String[]>() {
                            @Override
                            public void accept(String[] strings) throws Exception {
                                mMainModelProcessor.getBaike(strings[0],
                                        strings[1], strings[2], strings[3], strings[4]);
                            }
                        }).compose(bindUntilEvent(ActivityEvent.STOP)).subscribe();
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
            case "watermark":
                ImageUtils.addWatermark(
                        SDCardUtils.getSDCardPath() + "/IMG_20161027_124618.jpg",
                        DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"), 10,
                        ImageUtils.WatermarkLocation.BOTTOM_RIGHT,
                        bindUntilEvent(ActivityEvent.STOP)
                );
                break;
        }
    }
}
