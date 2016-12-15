package com.sunnybear.library.basic.presenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.widget.EditText;

import com.sunnybear.library.basic.bus.RxSubscriptions;
import com.sunnybear.library.basic.model.BindModel;
import com.sunnybear.library.basic.model.Model;
import com.sunnybear.library.basic.util.ActivityManager;
import com.sunnybear.library.basic.view.View;
import com.sunnybear.library.util.KeyboardUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ToastUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.ButterKnife;
import io.reactivex.Flowable;

/**
 * 基础FragmentActivity,主管模组分发
 * Created by sunnybear on 16/1/29.
 */
public abstract class PresenterActivity<VB extends View> extends RxAppCompatActivity implements Presenter {
    protected Context mContext;
    protected PresenterActivity mActivity;
    protected VB mViewBinder;
    private Bundle args;
    /*退出时间*/
    private long exitTime = 0;
    /*观察者管理器*/
    private Map<String, Flowable> mObservableMap;

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = (PresenterActivity) mContext;
        /*观察者管理器*/
        mObservableMap = new ConcurrentHashMap<>();
        args = getIntent().getExtras();
        onBundle(args);
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0) throw new RuntimeException("找不到Layout资源,Activity初始化失败");
        setContentView(layoutId);
        //声明ButterKnife
        ButterKnife.bind(mViewBinder, this);
        getModelProcessor();//获取ModelProcessor实例

        mViewBinder.onBindView(args != null ? args : new Bundle());
        mViewBinder.onViewCreatedFinish(savedInstanceState);
        mViewBinder.addListener();

        onViewBindFinish(savedInstanceState);

        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 获取观察者管理器
     *
     * @return 观察者管理器
     */
    @Override
    public final Map<String, Flowable> getObservables() {
        return mObservableMap;
    }

    /**
     * 设置Presenter实例,绑定View
     *
     * @param presenter 自己本身
     */
    protected abstract VB getViewBinder(Presenter presenter);

    /**
     * View绑定ViewBinder完成回调
     *
     * @param savedInstanceState savedInstanceState
     */
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 接收bundle传递参数
     *
     * @param args bundle参数
     */
    protected void onBundle(Bundle args) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewBinder.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewBinder.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewBinder.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewBinder.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewBinder.onDestroy();
        mViewBinder = null;
        mObservableMap.clear();
        mObservableMap = null;
        RxSubscriptions.clear();
        ActivityManager.getInstance().removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mViewBinder.onRestart();
    }

    /**
     * 获取model处理器
     *
     * @param <M> model处理器的泛型
     */
    private <M extends Model> void getModelProcessor() {
        M model;
        try {
            Class<?> self = this.getClass();
            Field[] fields = self.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(BindModel.class);
                if (annotation != null) {
                    if (field.getModifiers() != Modifier.PUBLIC) Logger.e("ModelProcessor不能使用修饰符");
                    Class<?> mc = field.getType();
                    Constructor<?> constructor = mc.getConstructor(PresenterActivity.class);
                    model = (M) constructor.newInstance(this);
                    field.setAccessible(true);
                    field.set(this, model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绑定Service
     *
     * @param targetClass 目标Service类型
     * @param connection  Service绑定连接
     */
    public void bindService(Class<? extends Service> targetClass, ServiceConnection connection) {
        Intent intent = new Intent(mContext, targetClass);
        super.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //判断软键盘是否展开
        if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            android.view.View view = getCurrentFocus();
            if (isShouldHideInput(view, ev))
                KeyboardUtils.closeKeyboard(mContext, view);
            return super.dispatchTouchEvent(ev);
        }
        //必不可少,否则所有的组件都不会有TouchEvent事件了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    /**
     * 是否隐藏软键盘
     *
     * @param view  对应View
     * @param event 事件
     */
    private boolean isShouldHideInput(android.view.View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            //获取输入框当前位置
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }


    /**
     * 双击退出app
     *
     * @param exit 退出间隔时间(毫秒数)
     */
    protected void exit(long exit) {
        if (System.currentTimeMillis() - exitTime > exit) {
            ToastUtils.showToastLong(mContext, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.getInstance().killProcess(mContext.getApplicationContext());
        }
    }

    /**
     * 双击退出app
     */
    protected void exit() {
        exit(2000);
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
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    protected final <T> void sendToView(String tag, T model) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(model)
                    .onBackpressureBuffer()));
        mViewBinder.receiveObservableFromPresenter(tag + TAG);
    }

    /**
     * 发送观察者
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    protected final <T> void sendToView(String tag, T... models) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> Flowable.just(models)
                    .onBackpressureBuffer()));
        mViewBinder.receiveObservableFromPresenter(tag + TAG);
    }

    /**
     * 发送观察者
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void sendToView(String tag, Flowable<T> observable) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Flowable.defer(() -> observable));
        mViewBinder.receiveObservableFromPresenter(tag + TAG);
    }

    /**
     * 发送一个动作
     *
     * @param tag 标签
     */
    protected final void sendToView(String tag) {
        mViewBinder.receiveObservableFromPresenter(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    protected final <T> Flowable<T> receiver(String tag) {
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
    protected final <T> Flowable<T> receiverArray(String tag) {
        Flowable<T[]> observable = (Flowable<T[]>) mObservableMap.remove(tag);
        if (observable != null)
            return observable.flatMap(ts -> Flowable.fromArray(ts).onBackpressureBuffer());
        return null;
    }

    /**
     * 接收观察者
     *
     * @param tag 观察者标签
     * @param <T> 泛型
     */
    protected final <T> Flowable<T> receiverFlowable(String tag) {
        return (Flowable<T>) mObservableMap.remove(tag);
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
