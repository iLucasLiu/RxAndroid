package com.sunnybear.rxandroid.model;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.RetrofitProvider;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.rxandroid.model.entity.Baike;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class MainModelProcessor extends ModelProcessor {
    private RequestService mRequestService;

    public MainModelProcessor(PresenterActivity activity) {
        super(activity);
        mRequestService = RetrofitProvider.create(RequestService.class);
    }

    public void getBaike(String scope, String format, String appid, String bk_key, String bk_length) {
        RequestHelper.request(
                mRequestService.getBaike(scope, format, appid, bk_key, bk_length),
                new RequestCallback<Baike>(mContext) {
                    @Override
                    public void onSuccess(Baike baike) {
                        mActivity.send("result", Flowable.just(baike)
                                .map(bk -> bk.toString()));
                    }

                    @Override
                    public void onFailure(int statusCode, String error) {
                        Logger.e(error);
                    }
                });
    }
}
