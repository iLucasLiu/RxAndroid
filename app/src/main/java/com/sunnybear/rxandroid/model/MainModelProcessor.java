package com.sunnybear.rxandroid.model;

import com.sunnybear.library.basic.model.ModelProcessor;
import com.sunnybear.library.basic.presenter.PresenterActivity;
import com.sunnybear.library.network.RequestHelper;
import com.sunnybear.library.network.RetrofitService;
import com.sunnybear.library.network.callback.RequestCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.rxandroid.db.dao.UserDao;
import com.sunnybear.rxandroid.db.entity.User;
import com.sunnybear.rxandroid.db.util.DatabaseOperation;
import com.sunnybear.rxandroid.model.entity.Baike;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by chenkai.gu on 2016/11/17.
 */
public class MainModelProcessor extends ModelProcessor {
    @RetrofitService
    RequestService mRequestService;
    private UserDao mUserDao;

    public MainModelProcessor(PresenterActivity activity) {
        super(activity);
        mUserDao = (UserDao) DatabaseOperation.getEntityDao(User.class, false);
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

    public void saveUser() {
        User user = new User();
        user.setName("sunny");
        user.setAddress("浙江杭州");
        user.setNickName("sunny");
        user.setMobilePhone("18888888888");
        user.setBirthday("1988-04-10");
        mUserDao.insertOrReplaceInTx(user);
    }

    public List<User> getUsers() {
        return mUserDao.loadAll();
    }
}
