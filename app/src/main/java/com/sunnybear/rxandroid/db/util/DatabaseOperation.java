package com.sunnybear.rxandroid.db.util;

import android.database.Cursor;
import android.os.Bundle;

import com.sunnybear.library.util.Logger;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 数据库操作
 * Created by chenkai.gu on 2016/10/8.
 */
public final class DatabaseOperation {
    private static AbstractDaoMaster sMaster;
    private static AbstractDaoSession mSession;

    /**
     * 获取实体类的Dao
     *
     * @param entity 实体类
     * @param isRead 是否是读数据或者写数据
     */
    public static AbstractDao getEntityDao(Class<? extends Serializable> entity, boolean isRead) {
        AbstractDao mDao = null;
        if (isRead)
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getReadableDb());
        else
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getWritableDb());
        mSession = sMaster.newSession();
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
     * 获取Database实例
     *
     * @param isRead 是否只读
     */
    private static Database getDatabase(boolean isRead) {
        if (isRead)
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getReadableDb());
        else
            sMaster = new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getWritableDb());
        return sMaster.newSession().getDatabase();
    }

    /**
     * 批量事务操作
     *
     * @param operation 事务操作
     */
    public static void batchTransactionOperation(Runnable operation) {
        new DaoMaster(DatabaseConfiguration.getDatabaseOpenHelper().getWritableDb())
                .newSession().runInTx(operation);
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
     * @param sql           原生sql
     * @param resultClass   接受类的类型
     * @param selectionArgs 查询条件参数
     * @param <T>           结果泛型
     */
    public static <T extends Serializable> T rawQueryUnique(String sql, Class<? extends Serializable> resultClass, String... selectionArgs) {
        T result = null;
        Cursor cursor = getDatabase(true).rawQuery(sql, selectionArgs);
        try {
            result = (T) resultClass.newInstance();
            while (cursor.moveToNext()) {
                cursorToObject(resultClass, result, cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 原生sql查询,返回唯一对象
     *
     * @param sql           原生sql
     * @param resultClass   接受类的类型
     * @param callback      异步查询回调
     * @param selectionArgs 查询条件参数
     * @param <T>           结果泛型
     */
    public static <T extends Serializable> void rawQueryUniqueAsync(String sql, final Class<? extends Serializable> resultClass,
                                                                    LifecycleTransformer<Bundle> transformer, final QueryCallback<T> callback,
                                                                    String... selectionArgs) {
        Bundle bundle = new Bundle();
        bundle.putString("sql", sql);
        bundle.putStringArray("selectionArgs", selectionArgs);
        Flowable.just(bundle).onBackpressureBuffer().compose(transformer)
                .map(bundle1 -> getDatabase(true).rawQuery(bundle.getString("sql"), bundle.getStringArray("selectionArgs")))
                .map(cursor -> {
                    T result = (T) resultClass.newInstance();
                    while (cursor.moveToNext()) {
                        cursorToObject(resultClass, result, cursor);
                    }
                    return result;
                })
                .compose(upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .subscribe(t -> callback.query(t), throwable -> callback.error(throwable));
    }

    /**
     * 原生sql查询,返回集合对象
     *
     * @param sql           原生sql
     * @param resultClass   接受类的类型
     * @param selectionArgs 查询条件参数
     * @param <T>           结果泛型
     */
    public static <T extends Serializable> List<T> rawQueryList(String sql, Class<? extends Serializable> resultClass, String... selectionArgs) {
        List<T> results = new ArrayList<>();
        Cursor cursor = getDatabase(true).rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            T result = null;
            try {
                result = (T) resultClass.newInstance();
                cursorToObject(resultClass, result, cursor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            results.add(result);
        }
        return results;
    }

    /**
     * 原生sql查询,返回集合对象
     *
     * @param sql           原生sql
     * @param resultClass   接受类的类型
     * @param callback      异步查询回调
     * @param selectionArgs 查询条件参数
     * @param <T>           结果泛型
     */
    public static <T extends Serializable> void rawQueryListAsync(String sql, final Class<? extends Serializable> resultClass,
                                                                  LifecycleTransformer<Bundle> transformer, final QueryCallback<List<T>> callback,
                                                                  String... selectionArgs) {
        Bundle bundle = new Bundle();
        bundle.putString("sql", sql);
        bundle.putStringArray("selectionArgs", selectionArgs);
        Flowable.just(bundle).onBackpressureBuffer().compose(transformer)
                .map(bundle1 -> getDatabase(true).rawQuery(bundle.getString("sql"), bundle.getStringArray("selectionArgs")))
                .map(cursor -> {
                    List<T> results = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        T result = (T) resultClass.newInstance();
                        cursorToObject(resultClass, result, cursor);
                        results.add(result);
                    }
                    return results;
                })
                .compose(upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .subscribe(ts -> callback.query(ts), throwable -> callback.error(throwable));
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
     * 数据库异步查询的回调
     *
     * @param <T> 结果泛型
     */
    public interface QueryCallback<T> {

        void query(T t);

        void error(Throwable throwable);
    }
}
