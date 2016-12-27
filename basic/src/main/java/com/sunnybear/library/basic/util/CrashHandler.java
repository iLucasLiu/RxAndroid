package com.sunnybear.library.basic.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.sunnybear.library.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * Created by sunnybear on 15/3/13.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "Activity";
    private Context mContext;
    private static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().toString();
    private String mSavePath;
    private static CrashHandler mInstance = new CrashHandler();

    /**
     * 单例模式，保证只有一个CustomCrashHandler实例存在
     *
     * @return
     */
    public static CrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 为我们的应用程序设置自定义Crash处理
     */
    public CrashHandler setCrashHandler(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        return this;
    }

    public CrashHandler setSavePath(String savePath) {
        mSavePath = savePath;
        return this;
    }

    /**
     * 异常发生时，系统回调的函数，我们在这里处理一些操作
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //将一些信息保存到SDCard中
        saveInfoToSD(mContext, ex);

        //提示用户程序即将退出
        showToast(mContext, "很抱歉，程序遭遇异常，即将退出！");
        try {
            thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActivityStackManager.getInstance().killProcess(mContext);
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(1);
    }

    /**
     * 显示提示信息，需要在线程中显示Toast
     *
     * @param context
     * @param msg
     */
    private void showToast(final Context context, final String msg) {
        new Thread(() -> {
            Looper.prepare();
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Looper.loop();
        }).start();
    }


    /**
     * 获取一些简单的信息,软件版本，手机版本，型号等信息存放在HashMap中
     *
     * @param context
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context context) {
        HashMap<String, String> map = new HashMap<>();
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", "" + mPackageInfo.versionCode);

        map.put("MODEL", "" + Build.MODEL);
        map.put("SDK_INT", "" + Build.VERSION.SDK_INT);
        map.put("PRODUCT", "" + Build.PRODUCT);
        return map;
    }


    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter mStringWriter = new StringWriter();
        PrintWriter mPrintWriter = new PrintWriter(mStringWriter);
        throwable.printStackTrace(mPrintWriter);
        mPrintWriter.close();

        Log.e(TAG, mStringWriter.toString());
        return mStringWriter.toString();
    }

    /**
     * 保存获取的 软件信息，设备信息和出错信息保存在SDCard中
     *
     * @param context
     * @param ex
     * @return
     */
    private String saveInfoToSD(Context context, Throwable ex) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : obtainSimpleInfo(context).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append(" = ").append(value).append("\n");
        }
        sb.append("=====异常信息=====").append('\n');
        sb.append(obtainExceptionInfo(ex));
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(!StringUtils.isEmpty(mSavePath) ? mSavePath : SDCARD_ROOT + File.separator + "crash" + File.separator);
            if (!dir.exists()) dir.mkdir();
            try {
                fileName = dir.toString() + File.separator + parseTime(System.currentTimeMillis()) + ".log";
                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }


    /**
     * 将毫秒数转换成yyyy-MM-dd-HH-mm-ss的格式
     *
     * @param milliseconds
     * @return
     */
    private String parseTime(long milliseconds) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = format.format(new Date(milliseconds));
        return times;
    }
}
