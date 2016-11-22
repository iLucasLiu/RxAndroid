package com.sunnybear.library.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.network.interceptor.ProgressResponseInterceptor;
import com.sunnybear.library.util.FileUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

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
        Flowable.empty()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
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
    public static <T extends Serializable> void request(Flowable<T> call, final RequestCallback<T> callback,
                                                        LifecycleTransformer<T> transformer) {
        callback.onStart();
        call.compose(new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        }).compose(transformer)
                .onErrorResumeNext(new Function<Throwable, Publisher<? extends T>>() {
                    @Override
                    public Publisher<? extends T> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException)
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                        else
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_500, throwable.getMessage());
                        callback.onFinish(false);
                        return Flowable.empty();
                    }
                }).subscribe(new Consumer<T>() {
            @Override
            public void accept(T t) throws Exception {
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(FileOperationService.class);
        service.download(url)
                .subscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, File>() {
                    @Override
                    public File apply(ResponseBody responseBody) throws Exception {
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
                .onErrorResumeNext(new Function<Throwable, Publisher<? extends File>>() {
                    @Override
                    public Publisher<? extends File> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof ResponseFailureException) {
                            ResponseFailureException exception = (ResponseFailureException) throwable;
                            callback.onFailure(exception.getStatusCode(), exception.getMessage());
                        } else {
                            callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                        }
                        callback.onFinish(false);
                        return Flowable.empty();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<File>() {
                    @Override
                    public void accept(File file) throws Exception {
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
