package com.sunnybear.library.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.sunnybear.library.network.callback.DownloadCallback;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.network.callback.UploadCallback;
import com.sunnybear.library.network.file.DownloadInfo;
import com.sunnybear.library.network.file.FileOperationService;
import com.sunnybear.library.network.interceptor.ProgressResponseInterceptor;
import com.sunnybear.library.network.progress.ProgressRequestBody;
import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.PreferenceHelper;
import com.trello.rxlifecycle2.LifecycleTransformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 请求
 * Created by chenkai.gu on 2016/11/12.
 */
public final class RequestHelper {
    private static Disposable mDownloadDisposable;
    private static Map<String, Call<ResponseBody>> mDownloadCall = new ConcurrentHashMap<>();

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
     * @param isCover     是否覆盖已下载文件
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
     * @param isCover  是否覆盖已下载文件
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
     * 下载文件
     *
     * @param url      下载文件的地址
     * @param filePath 文件保存路径
     * @param callback 下载完成后的回调
     * @param isCover  是否覆盖已下载文件
     */
    public static void breakpointDownload(String url, String filePath, final DownloadCallback callback, boolean isCover) {
        callback.onStart();
        if (FileUtils.isExists(filePath) && !isCover) {
            Flowable.empty().observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_205, "不覆盖下载过的文件");
                        callback.onFinish(false);
                    }).subscribe();
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
        DownloadInfo downloadInfo = PreferenceHelper.getValue(url, new DownloadInfo(url, filePath, 0L, 0L));
        Call<ResponseBody> call = service.breakpointDownload(url, String.valueOf(downloadInfo.getStartPosition()));
        mDownloadCall.put(url, call);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response == null) {
                    callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_500, "responseBody为空");
                    callback.onFinish(false);
                } else if (!response.isSuccessful()) {
                    callback.onFailure(response.code(), response.message());
                    callback.onFinish(false);
                } else {
                    saveFile(response.body(), filePath, downloadInfo.getStartPosition());
                    callback.onSuccess(FileUtils.getFile(filePath));
                    callback.onFinish(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                if (throwable instanceof ResponseFailureException) {
                    ResponseFailureException exception = (ResponseFailureException) throwable;
                    callback.onFailure(exception.getStatusCode(), exception.getMessage());
                } else {
                    callback.onFailure(RetrofitProvider.StatusCode.STATUS_CODE_404, throwable.getMessage());
                }
                callback.onFinish(false);
            }
        });
    }

    /**
     * 停止断点下载
     *
     * @param url 下载地址
     */
    public static void breakpointCancel(String url) {
        if (mDownloadCall.containsKey(url))
            mDownloadCall.get(url).cancel();
    }

    /**
     * 断点保存文件
     *
     * @param responseBody  responseBody
     * @param filePath      文件路径
     * @param startPosition 开始点
     */
    private static void saveFile(ResponseBody responseBody, String filePath, long startPosition) {
        InputStream in = responseBody.byteStream();
        FileChannel fileChannel = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile accessFile = null;
        try {
            File file = new File(filePath);
            if (!FileUtils.isExists(file)) FileUtils.createFile(file);
            accessFile = new RandomAccessFile(file, "rwd");
            //Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
            fileChannel = accessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startPosition, responseBody.contentLength());
            byte[] buffer = new byte[4096];
            int len;
            while ((len = in.read(buffer)) != -1) {
                mappedByteBuffer.put(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                if (fileChannel != null) fileChannel.close();
                if (accessFile != null) accessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
