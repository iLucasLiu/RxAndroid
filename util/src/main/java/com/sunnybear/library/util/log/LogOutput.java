package com.sunnybear.library.util.log;

import org.apache.log4j.Logger;

/**
 * 日志输出工具
 * Created by chenkai.gu on 2016/12/2.
 */
public final class LogOutput {
    private static Logger mLogger;

    public static void initialize(Class<?> aClass) {
        mLogger = Logger.getLogger(aClass);
    }

    public static Logger getLogger() {
        return mLogger;
    }
}
