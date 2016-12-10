package com.sunnybear.library.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.network.callback.UploadCallback;
import com.sunnybear.library.network.file.FileOperationService;
import com.sunnybear.library.network.interceptor.ProgressResponseInterceptor;
import com.sunnybear.library.network.progress.ProgressRequestBody;
import com.sunnybear.library.util.FileUtils;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.File;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * 请求
 * Created by chenkai.gu on 2016/11/12.
 */
public final class RequestHelper {
    private static Disposable mDownloadDisposable;

    /**
     * 网络请求
     *
     * @param <T>      结果泛型
     * @param call     网络call
     * @param callback 网络请求回调
     */
    public static <T extends Serializable> void request(Call<T> call, RequestCallback<T> callback) {
        callback.onStart();
        call.enqueue(callback);
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
        call.onBackpressureBuffer()
                .compose(transformer)
                .compose(switchThread(Schedulers.computation()))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException)
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                    else
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_500, throwable.getMessage());
                    callback.onFinish(false);
                    return Flowable.empty();
                })
                .subscribe(t -> {
                    callback.onSuccess(t);
                    callback.onFinish(true);
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
            Flowable.empty().observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_205, "不覆盖下载过的文件");
                        callback.onFinish(false);
                    }).subscribe();
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
        service.download(url).onBackpressureBuffer()
                .map(responseBody -> {
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
                })
                .compose(transformer)
                .compose(switchThread(Schedulers.io()))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof ResponseFailureException) {
                        ResponseFailureException exception = (ResponseFailureException) throwable;
                        callback.onFailure(exception.getStatusCode(), exception.getMessage());
                    } else {
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                    }
                    callback.onFinish(false);
                    return Flowable.empty();
                })
                .subscribe(file -> {
                    callback.onSuccess(file);
                    callback.onFinish(true);
                });
    }


    /**
     * 下载文件
     *
     * @param url      下载文件的地址
     * @param filePath 文件保存路径
     * @param callback 下载完成后的回调
     * @return Disposable 处理实例
     */
    public static Disposable download(String url, final String filePath, final DownloadCallback callback, boolean isCover) {
        callback.onStart();
        if (FileUtils.isExists(filePath) && !isCover) {
            Flowable.empty().observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_205, "不覆盖下载过的文件");
                        callback.onFinish(false);
                    }).subscribe();
            return mDownloadDisposable = null;
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
        mDownloadDisposable = service.download(url).onBackpressureBuffer()
                .map(responseBody -> {
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
                })
                .compose(switchThread(Schedulers.io()))
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof ResponseFailureException) {
                        ResponseFailureException exception = (ResponseFailureException) throwable;
                        callback.onFailure(exception.getStatusCode(), exception.getMessage());
                    } else {
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                    }
                    callback.onFinish(false);
                    if (mDownloadDisposable != null && !mDownloadDisposable.isDisposed())
                        mDownloadDisposable.dispose();
                    return Flowable.empty();
                })
                .subscribe(file -> {
                    callback.onSuccess(file);
                    callback.onFinish(true);
                    if (mDownloadDisposable != null && !mDownloadDisposable.isDisposed())
                        mDownloadDisposable.dispose();
                });
        return mDownloadDisposable;
    }

    /**
     * 上传文件
     *
     * @param url      上传文件的地址
     * @param params   上传文件的参数
     * @param callback 上传文件回调
     * @param <T>      上传返回对象泛型
     */
    public static <T extends Serializable> void upload(String url, Map<String, Serializable> params, UploadCallback<T> callback) {
        callback.onStart();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Map.Entry<String, Serializable> param : params.entrySet()) {
            String name = param.getKey();
            Serializable value = param.getValue();
            if (value instanceof String) {
                builder.addFormDataPart(name, (String) value);
            } else if (value instanceof File) {
                File file = (File) value;
                builder.addPart(Headers.of("Content-Disposition"
                        , "form-data; name=\"" + name + "\";filename=\"" + FileUtils.getFileName(file.getAbsolutePath()) + "\"")
                        , RequestBody.create(MediaType.parse("application/octet-stream"), file));
            }
            RequestBody requestBody = new ProgressRequestBody(builder.build(), callback);
            FileOperationService service = RetrofitProvider.create(FileOperationService.class);
            Call<T> call = service.upload(url, requestBody);
            call.enqueue(callback);
        }
    }

    /**
     * 切换线程
     *
     * @param scheduler 处理线程
     * @param <T>       泛型
     */
    private static <T> FlowableTransformer<T, T> switchThread(Scheduler scheduler) {
        return upstream -> upstream.subscribeOn(scheduler).observeOn(AndroidSchedulers.mainThread());
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
