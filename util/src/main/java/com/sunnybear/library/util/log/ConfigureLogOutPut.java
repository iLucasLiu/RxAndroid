package com.sunnybear.library.util.log;

import org.apache.log4j.Level;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * 配置输出日志文件
 * Created by chenkai.gu on 2016/12/2.
 */
public final class ConfigureLogOutput {

    /**
     * 配置
     *
     * @param outputPath  输出文件名
     * @param loggerName  项目包名
     * @param maxFileSize 输入文件大小
     * @param level       输出级别
     */
    public static void configure(String outputPath, String loggerName, long maxFileSize, Level level,
                                 String pattern) {
        final LogConfigurator configurator = new LogConfigurator();
        //设置文件名
        configurator.setFileName(outputPath);
        //设置root日志输出级别 默认为DEBUG
        configurator.setRootLevel(Level.DEBUG);
        // 设置日志输出级别
        configurator.setLevel(loggerName, level);
        //设置 输出到日志文件的文字格式 默认 %d %-5p [%c{2}]-[%L] %m%n
        configurator.setFilePattern(pattern);
        //设置输出到控制台的文字格式 默认%m%n
        configurator.setLogCatPattern(pattern);
        //设置总文件大小
        configurator.setMaxFileSize(maxFileSize);
        //设置最大产生的文件个数
        configurator.setMaxBackupSize(1);
        //设置所有消息是否被立刻输出 默认为true,false 不输出
        configurator.setImmediateFlush(true);
        //是否本地控制台打印输出 默认为true ，false不输出
        configurator.setUseLogCatAppender(false);
        //设置是否启用文件附加,默认为true。false为覆盖文件
        configurator.setUseFileAppender(true);
        //设置是否重置配置文件，默认为true
        configurator.setResetConfiguration(true);
        //是否显示内部初始化日志,默认为false
        configurator.setInternalDebugging(false);
        configurator.configure();
    }
}
