package com.sunnybear.library.basic.presenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.view.MotionEvent;
import android.widget.EditText;

import com.sunnybear.library.basic.ActivityManager;
import com.sunnybear.library.basic.model.InjectModel;
import com.sunnybear.library.basic.model.Model;
import com.sunnybear.library.basic.view.View;
import com.sunnybear.library.basic.view.ViewBinder;
import com.sunnybear.library.eventbus.EventBusHelper;
import com.sunnybear.library.util.KeyboardUtils;
import com.sunnybear.library.util.ToastUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

/**
 * 基础FragmentActivity,主管模组分发
 * Created by sunnybear on 16/1/29.
 */
public abstract class PresenterActivity<VB extends View> extends RxAppCompatActivity
        implements Presenter {
    protected Context mContext;
    protected VB mViewBinder;
    private Bundle args;
    /*退出时间*/
    private long exitTime = 0;
    /*观察者管理器*/
    private Map<String, Observable> mObservableMap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        /*观察者管理器*/
        mObservableMap = new ArrayMap<>();
        mViewBinder = getViewBinder(this);
        int layoutId = mViewBinder.getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Activity初始化失败");
        setContentView(layoutId);
        //声明ButterKnife
        ButterKnife.bind(mViewBinder, this);
        getModelProcessor();//获取ModelProcessor实例

        args = getIntent().getExtras();
        mViewBinder.onBindView(args != null ? args : new Bundle());
        mViewBinder.onViewCreatedFinish();
        mViewBinder.addListener();

        onViewBindFinish(savedInstanceState);
        //注册EventBus
        EventBusHelper.register(this);

        ActivityManager.getInstance().addActivity(this);
    }

    /**
     * 获取观察者管理器
     *
     * @return 观察者管理器
     */
    public final Map<String, Observable> getObservables() {
        return mObservableMap;
    }

    /**
     * 设置Presenter实例,绑定View
     *
     * @param context 上下文
     */
    protected abstract VB getViewBinder(Context context);

    /**
     * View绑定ViewBinder完成回调
     *
     * @param savedInstanceState savedInstanceState
     */
    protected void onViewBindFinish(@Nullable Bundle savedInstanceState) {

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
        EventBusHelper.unregister(this);
        mViewBinder.onDestroy();
        mViewBinder = null;
        mObservableMap.clear();
        mObservableMap = null;
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
    protected  <M extends Model> void getModelProcessor() {
        M model = null;
        try {
            Class<?> self = this.getClass();
            Field[] fields = self.getDeclaredFields();
            for (Field field : fields) {
                Annotation annotation = field.getAnnotation(InjectModel.class);
                if (annotation != null) {
                    InjectModel injectModel = (InjectModel) annotation;
                    Class<?> mc = injectModel.value();
                    Constructor<?> constructor = mc.getConstructor(Context.class);
                    model = (M) constructor.newInstance(mContext);
                }
                field.setAccessible(true);
                field.set(this, model);
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
        /*if (ev.getAction() == MotionEvent.ACTION_DOWN)
            mLastActionTime = System.currentTimeMillis();*/
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
     * 接收View层的的观察者并处理
     *
     * @param tag 观察者标签
     */
    @Override
    public void receiveObservableModel(String tag) {

    }

    /**
     * 接收Model的观察者并处理
     *
     * @param tag 观察者标签
     */
    @Override
    public void receiveObservableView(String tag) {

    }

    /**
     * 发送观察者
     *
     * @param tag   标签
     * @param model 数据Model
     * @param <T>   泛型
     */
    public final <T> void send(String tag, T model) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Observable.just(model));
        ((ViewBinder) mViewBinder).receiveObservable(tag + TAG);
    }

    /**
     * 发送观察者
     *
     * @param tag    标签
     * @param models 数据Model组
     * @param <T>    泛型
     */
    public final <T> void send(String tag, T... models) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, Observable.just(models));
        ((ViewBinder) mViewBinder).receiveObservable(tag + TAG);
    }

    /**
     * 发送观察者
     *
     * @param tag        标签
     * @param observable 数据Model组
     * @param <T>        泛型
     */
    public final <T> void send(String tag, Observable<T> observable) {
        if (!mObservableMap.containsKey(tag + TAG))
            mObservableMap.put(tag + TAG, observable);
        ((ViewBinder) mViewBinder).receiveObservable(tag + TAG);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    protected final <T> Observable<T> receive(String tag, ActivityEvent event) {
        Observable<T> observable = (Observable<T>) mObservableMap.remove(tag);
        if (observable != null)
            if (event != null)
                return observable.compose(this.<T>bindUntilEvent(event));
            else
                return observable.compose(this.<T>bindToLifecycle());
        return null;
    }

    /**
     * 接收观察者并处理(全生命周期管理RxJava线程)
     *
     * @param tag 观察者标签
     */
    protected final <T> Observable<T> receive(String tag) {
        return receive(tag, null);
    }

    /**
     * 接收观察者并处理
     *
     * @param tag   观察者标签
     * @param event 在Activity那个生命周期结束RxJava线程
     */
    protected final <T> Observable<T> receiveArray(String tag, ActivityEvent event) {
        Observable<T[]> observable = (Observable<T[]>) mObservableMap.remove(tag);
        if (observable != null)
            if (event != null)
                return observable.compose(this.<T[]>bindUntilEvent(event))
                        .flatMap(new Func1<T[], Observable<T>>() {
                            @Override
                            public Observable<T> call(T[] ts) {
                                return Observable.from(ts);
                            }
                        });
            else
                return observable.compose(this.<T[]>bindToLifecycle())
                        .flatMap(new Func1<T[], Observable<T>>() {
                            @Override
                            public Observable<T> call(T[] ts) {
                                return Observable.from(ts);
                            }
                        });
        return null;
    }

    /**
     * 接收观察者并处理
     *
     * @param tag 观察者标签
     */
    protected final <T> Observable<T> receiveArray(String tag) {
        return receiveArray(tag, null);
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
