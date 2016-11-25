package com.sunnybear.library.basic.presenter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.sunnybear.library.basic.bus.RxSubscriptions;
import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.model.Model;
import com.sunnybear.library.basic.view.View;
import com.sunnybear.library.basic.view.ViewBinder;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.Flowable;

/**
 * 基础Fragment,主管模组分发
 * Created by sunnybear on 16/1/29.
 */
public abstract class PresenterFragment<VB extends View> extends RxFragment implements Presenter {
    protected Context mContext;
    protected VB mViewBinder;
    private PresenterActivity mActivity;
    protected PresenterFragment mFragment;

    private Bundle args;
    private android.view.View mFragmentView = null;
    /*观察者管理器*/
    private Map<String, Flowable> mObservableMap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof PresenterActivity))
            throw new RuntimeException("必须依赖PresenterActivity");
        mContext = context;
        /*观察者管理器*/
        mObservableMap = mActivity.getObservables();
    }

    /**
     * 获取观察者管理器
     *
     * @return 观察者管理器
     */
    public final Map<String, Flowable> getObservables() {
        return mActivity.getObservables();
    }

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
        getModelProcessor();
    }

    /**
     * 获取model处理器
     *
     * @param <M> model处理器的泛型
     */
    private <M extends Model> void getModelProcessor() {
        M model = null;
        try {
            Class<?> self = this.getClass();
            Field[] fields = self.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(InjectModel.class);
                if (annotation != null) {
                    Class<?> mc = field.getType();
                    Constructor<?> constructor = mc.getConstructor(PresenterFragment.class);
                    model = (M) constructor.newInstance(this);
                }
                field.setAccessible(true);
                field.set(this, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public final android.view.View onCreateView(LayoutInflater inflater,
                                                @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Fragment初始化失败");
        mFragmentView = inflater.inflate(layoutId, container, false);
        ViewGroup parent = (ViewGroup) mFragmentView.getParent();
        if (parent != null)
            parent.removeView(mFragmentView);
        ButterKnife.bind(mViewBinder, mFragmentView);
        onViewCreatedFinish(savedInstanceState);
        return mFragmentView;
    }

    @Override
    public final void onViewCreated(android.view.View view, @Nullable Bundle
            savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (PresenterActivity) getActivity();
        mFragment = this;
        mViewBinder.onBindView(args != null ? args : new Bundle());
        mViewBinder.onViewCreatedFinish();
        mViewBinder.addListener();
    }

    /**
     * View创建完成后回调
     *
     * @param savedInstanceState savedInstanceState
     */

    protected void onViewCreatedFinish(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 设置Presenter实例,绑定View
     *
     * @param presenter 自己本身
     */
    protected abstract VB getViewBinder(Presenter presenter);

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        mViewBinder.onDestroy();
        mViewBinder = null;
        mObservableMap.clear();
        mObservableMap = null;
        RxSubscriptions.clear();
        if (mFragmentView != null)
            ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
    }

    /**
     * 接收Model的观察者并处理
     *
     * @param tag 观察者标签
     */
    @Override
    public void receiveObservableFromView(String tag) {

    }

    /**
     * 发送观察者
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void send(String tag, T models) {
        if (!mObservableMap.containsKey(tag))
            mObservableMap.put(tag, Flowable.defer(() -> Flowable.just(models)
                    .onBackpressureBuffer()));
    }

    /**
     * 发送观察者
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void send(String tag, T... models) {
        if (!mObservableMap.containsKey(tag))
            mObservableMap.put(tag, Flowable.defer(() -> Flowable.just(models)
                    .onBackpressureBuffer()));
    }

    /**
     * 发送观察者
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void send(String tag, Flowable<T> observable) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> observable));
        ((ViewBinder) mViewBinder).receiveObservable(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receive(String tag) {
        Flowable<T> observable = (Flowable<T>) mObservableMap.remove(tag);
        if (observable != null)
            return observable.onBackpressureBuffer();
        return null;
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    public final <T> Flowable<T> receiveArray(String tag) {
        Flowable<T[]> observable = (Flowable<T[]>) mObservableMap.remove(tag);
        if (observable != null)
            return observable.flatMap(ts -> Flowable.fromArray(ts).onBackpressureBuffer());
        return null;
    }

    /**
     * 过滤标签
     *
     * @param tag 标签
     * @return 过滤后的标签
     */
    protected String filterTag(String tag) {
        return tag.split("\\-")[0];
    }
}
