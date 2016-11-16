package com.sunnybear.library.network;

import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.network.interceptor.ProgressResponseInterceptor;
import com.sunnybear.library.util.FileUtils;
import com.trello.rxlifecycle.LifecycleTransformer;

import java.io.File;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 请求
 * Created by chenkai.gu on 2016/11/12.
 */
public final class RequestHelper {

    /**
     * 网络请求
     *
     * @param <T>         结果泛型
     * @param call        网络call
     * @param callback    网络请求回调
     * @param transformer 管理生命周期,防止内存泄漏
     */
    public static <T extends Serializable> void request(final Call<T> call, final RequestCallback<T> callback,
                                                        LifecycleTransformer<Object> transformer) {
        Observable.empty()
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        callback.onStart();
                        call.enqueue(callback);
                    }
                }).compose(transformer).subscribe();
    }

    /**
     * 网络请求
     *
     * @param <T>         结果泛型
     * @param call        网络call
     * @param callback    网络请求回调
     * @param transformer 管理生命周期,防止内存泄漏
     */
    public static <T extends Serializable> void request(final Observable<T> call, final RequestCallback<T> callback,
                                                        LifecycleTransformer<T> transformer) {
        callback.onStart();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(transformer)
                .subscribe(new Subscriber<T>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof UnknownHostException || e instanceof SocketTimeoutException)
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, e.getMessage());
                        else
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_500, e.getMessage());
                        callback.onFinish(false);
                    }

                    @Override
                    public void onNext(T t) {
                        callback.onSuccess(t);
                        callback.onFinish(true);
                    }
                });
    }

    /**
     * 下载文件
     *
     * @param url         下载文件的地址
     * @param filePath    文件保存路径
     * @param callback    下载完成后的回调
     * @param transformer 管理生命周期,防止内存泄漏
     */
    public static void download(String url, final String filePath, final DownloadCallback callback,
                                LifecycleTransformer<File> transformer, boolean isCover) {
        callback.onStart();
        if (FileUtils.isExists(filePath) && !isCover) {
            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_205, "不覆盖下载过的文件");
            callback.onFinish(false);
            return;
        }
        Retrofit retrofit = RetrofitProvider.getRetrofit();
        OkHttpClient mOkHttpClient = (OkHttpClient) retrofit.callFactory();
        OkHttpClient.Builder builder = mOkHttpClient.newBuilder().addInterceptor(
                new ProgressResponseInterceptor(callback));
        FileOperationService service = new Retrofit.Builder()
                .baseUrl(retrofit.baseUrl())
                .client(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(FileOperationService.class);
        service.download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, File>() {
                    @Override
                    public File call(ResponseBody responseBody) {
                        File file = null;
                        if (responseBody != null)
                            file = FileUtils.saveFile(responseBody.byteStream(), filePath);
                        else
                            try {
                                throw new ResponseFailureException(RetrofitProvider.StatusCode.STATUS_CODE_500,
                                        "responseBody为空");
                            } catch (ResponseFailureException e) {
                                e.printStackTrace();
                            }
                        return file;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends File>>() {
                    @Override
                    public Observable<? extends File> call(Throwable throwable) {
                        if (throwable instanceof ResponseFailureException) {
                            ResponseFailureException exception = (ResponseFailureException) throwable;
                            callback.onFailure(exception.getStatusCode(), exception.getMessage());
                        } else {
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                        }
                        return Observable.empty();
                    }
                })
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        callback.onSuccess(file);
                        callback.onFinish(true);
                    }
                }).compose(transformer).subscribe();
    }

    /**
     * Response请求失败异常
     * Created by chenkai.gu on 2016/11/12.
     */
    private static class ResponseFailureException extends Exception {
        private int statusCode;

        private ResponseFailureException(int statusCode, String detailMessage) {
            super(detailMessage);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
