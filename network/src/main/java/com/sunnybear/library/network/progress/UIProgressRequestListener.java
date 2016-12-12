package com.sunnybear.library.network.progress;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * 请求体回调实现类，用于UI层回调
 * Created by guchenkai on 2015/10/26.
 */
public abstract class UIProgressRequestListener implements ProgressRequestListener {
    //    private static final int REQUEST_UPDATE = 0x01;
    //主线程Handler
//    private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);
    private Disposable mDisposable;

    /**
     * 处理UI层的Handler子类
     */
    /*private static class UIHandler extends Handler {
        private WeakReference<UIProgressRequestListener> mUIUiProgressRequestListenerWeakReference;

        public UIHandler(Looper looper, UIProgressRequestListener uiProgressRequestListener) {
            super(looper);
            mUIUiProgressRequestListenerWeakReference = new WeakReference<>(uiProgressRequestListener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_UPDATE:
                    UIProgressRequestListener uiProgressRequestListener = mUIUiProgressRequestListenerWeakReference.get();
                    if (uiProgressRequestListener != null) {
                        ProgressModel progressModel = (ProgressModel) msg.obj; //获得进度实体类
                        uiProgressRequestListener.onUIRequestProgress(progressModel.getCurrentBytes(), progressModel.getContentLength(), progressModel.isDone());
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }*/
    @Override
    public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
//        //通过Handler发送进度消息
//        Message message = Message.obtain();
//        message.obj = new ProgressModel(bytesWritten, contentLength, done);
//        message.what = REQUEST_UPDATE;
//        mHandler.sendMessage(message);
        mDisposable = Flowable.just(new ProgressModel(bytesWritten, contentLength, done)).onBackpressureBuffer()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(model ->
                        onUIRequestProgress(model.getCurrentBytes(), model.getContentLength(), model.isDone()))
                .doOnComplete(() -> {
                    if (done && !mDisposable.isDisposed()) mDisposable.dispose();
                })
                .subscribe();
    }

    /**
     * UI层回调抽象方法
     *
     * @param bytesWrite    当前写入的字节长度
     * @param contentLength 总字节长度
     * @param done          是否写入完成
     */
    public abstract void onUIRequestProgress(long bytesWrite, long contentLength, boolean done);
}
