package com.sunnybear.rxandroid.db.util;

import android.database.Cursor;
import android.os.Bundle;

import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.RxPlugin;
import com.sunnybear.rxandroid.db.dao.DaoMaster;
import com.trello.rxlifecycle2.LifecycleTransformer;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * 数据库操作
 * Created by chenkai.gu on 2016/10/8.
 */
public final class DatabaseOperation {

    /**
     * 获取实体类的Dao
     *
     * @param entity 实体类
     * @param isRead 是否是读数据或者写数据
     */
    public static AbstractDao getEntityDao(Class<? extends Serializable> entity, boolean isRead) {
        AbstractDao mDao = null;
        AbstractDaoSession mSession = getSession(isRead);
        try {
            String entityName = entity.getSimpleName();
            Class<?> mSessionClass = mSession.getClass();
            Method getDao = mSessionClass.getDeclaredMethod("get" + entityName + "Dao");
            mDao = (AbstractDao) getDao.invoke(mSession);
        } catch (Exception e) {
            Logger.e("获得Dao错误", e);
            e.printStackTrace();
        }
        return mDao;
    }

    /**
     * 获得AbstractDaoSession
     *
     * @param isRead 是否只读
     */
    private static AbstractDaoSession getSession(boolean isRead) {
        AbstractDaoMaster sMaster = null;
        if (isRead)
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getReadableDb());
        else
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getWritableDb());
        return sMaster.newSession();
    }

    /**
     * 获取Database实例
     *
     * @param isRead 是否只读
     */
    private static Database getDatabase(boolean isRead) {
        return getSession(isRead).getDatabase();
    }

    /**
     * 批量事务操作
     *
     * @param operation 事务操作
     */
    public static void batchTransactionOperation(Runnable operation) {
        getSession(false).runInTx(operation);
    }

    /**
     * 删除表
     *
     * @param tableName 表名
     */
    public static void dropTable(String tableName) {
        String sql = "DROP TABLE " + "\"" + tableName + "\"";
        getDatabase(false).execSQL(sql);
    }

    /**
     * 原生sql查询,返回唯一对象
     *
     * @param sql         原生sql
     * @param resultClass 接受类的类型
     * @param callback    异步查询回调
     * @param <T>         结果泛型
     */
    public static <T extends Serializable> void rawQueryUnique(Bundle sql, final Class<? extends Serializable> resultClass,
                                                               final QueryCallback<T> callback, LifecycleTransformer<Bundle> transformer) {
        callback.onStart();
        Flowable.just(sql).onBackpressureBuffer().compose(transformer)
                .map(bundle -> {
                    String sqlString = bundle.getString(SQL.KEY_SQL);
                    String[] selectionArgs = bundle.getStringArray(SQL.KEY_SELECTION_ARGS);
                    return getDatabase(true).rawQuery(sqlString, selectionArgs != null ? selectionArgs : new String[]{});
                })
                .map(cursor -> {
                    T result = (T) resultClass.newInstance();
                    while (cursor.moveToNext()) {
                        cursorToObject(resultClass, result, cursor);
                    }
                    return result;
                })
                .compose(RxPlugin.switchThread(Schedulers.io()))
                .doOnNext(t -> {
                    callback.onQuerySuccess((T) t);
                    callback.onFinish(true);
                })
                .doOnError(throwable -> {
                    callback.onError(throwable);
                    callback.onFinish(false);
                })
                .subscribe();
    }

    /**
     * 原生sql查询,返回集合对象
     *
     * @param sql         原生sql
     * @param resultClass 接受类的类型
     * @param callback    异步查询回调
     * @param <T>         结果泛型
     */
    public static <T extends Serializable> void rawQueryList(Bundle sql, final Class<? extends Serializable> resultClass,
                                                             final QueryCallback<List<T>> callback, LifecycleTransformer<Bundle> transformer) {
        callback.onStart();
        Flowable.just(sql).onBackpressureBuffer().compose(transformer)
                .map(bundle -> {
                    String sqlString = bundle.getString(SQL.KEY_SQL);
                    String[] selectionArgs = bundle.getStringArray(SQL.KEY_SELECTION_ARGS);
                    return getDatabase(true).rawQuery(sqlString, selectionArgs != null ? selectionArgs : new String[]{});
                })
                .map(cursor -> {
                    List<T> results = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        T result = (T) resultClass.newInstance();
                        cursorToObject(resultClass, result, cursor);
                        results.add(result);
                    }
                    return results;
                })
                .compose(RxPlugin.switchThread(Schedulers.io()))
                .doOnNext(t -> {
                    callback.onQuerySuccess((List<T>) t);
                    callback.onFinish(true);
                })
                .doOnError(throwable -> {
                    callback.onError(throwable);
                    callback.onFinish(false);
                })
                .subscribe();
    }

    /**
     * 游标数据转换对象
     *
     * @param resultClass 对象类型
     * @param result      对象实体
     * @param cursor      游标
     * @param <T>         结果泛型
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static <T extends Serializable> void cursorToObject(Class<? extends Serializable> resultClass, T result, Cursor cursor) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Field[] fields = resultClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String name = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String type = field.getGenericType().toString();
            Method method = null;
            Ignore ignore = field.getAnnotation(Ignore.class);
            if (ignore == null)
                switch (type) {
                    case "class java.lang.String":
                        method = resultClass.getMethod("set" + name, String.class);
                        method.invoke(result, cursor.getString(cursor.getColumnIndexOrThrow(fieldName)));
                        break;
                    case "class java.lang.Integer":
                        method = resultClass.getMethod("set" + name, Integer.class);
                        method.invoke(result, cursor.getInt(cursor.getColumnIndexOrThrow(fieldName)));
                        break;
                    case "class java.lang.Long":
                        method = resultClass.getMethod("set" + name, Long.class);
                        method.invoke(result, cursor.getLong(cursor.getColumnIndexOrThrow(fieldName)));
                        break;
                    case "class java.lang.Double":
                        method = resultClass.getMethod("set" + name, Double.class);
                        method.invoke(result, cursor.getDouble(cursor.getColumnIndexOrThrow(fieldName)));
                        break;
                }
        }
    }

    /**
     * 组装SQL语句和条件值
     */
    public static final class SQL {
        public static final String KEY_SQL = "sql";
        public static final String KEY_SELECTION_ARGS = "selectionArgs";

        /**
         * 组装SQL语句和条件值
         *
         * @param sql           原生sql
         * @param selectionArgs 查询条件参数
         * @return
         */
        public static Bundle assemble(String sql, String... selectionArgs) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_SQL, sql);
            bundle.putStringArray(KEY_SELECTION_ARGS, selectionArgs);
            return bundle;
        }
    }

    /**
     * 数据库异步查询的回调
     *
     * @param <T> 结果泛型
     */
    public interface QueryCallback<T> {

        void onStart();

        void onQuerySuccess(T t);

        void onError(Throwable throwable);

        void onFinish(boolean isSuccess);
    }
}
