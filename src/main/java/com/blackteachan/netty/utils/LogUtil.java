package com.blackteachan.netty.utils;

import org.apache.log4j.Logger;

/**
 * 日志工具类
 * @author blackteachan
 */
public class LogUtil {

    private static LogType mLogType = LogType.INFO;
    private static String mShowClassName = "";

    private Logger log;
    private final String mClassName;

    public LogUtil(Class<?> clazz){
        log = Logger.getLogger(clazz);
        mClassName = clazz.getSimpleName();
    }

    public void e(Object message){
        if((mLogType==LogType.ALL || mLogType==LogType.ERROR) && ("".equals(mShowClassName) || mClassName.equals(mShowClassName))) {
            log.error(message);
        }
    }

    public void w(Object message){
        if((mLogType==LogType.ALL || mLogType==LogType.WARN) && ("".equals(mShowClassName) || mClassName.equals(mShowClassName))) {
            log.warn(message);
        }
    }

    public void i(Object message){
        if((mLogType==LogType.ALL || mLogType==LogType.INFO) && ("".equals(mShowClassName) || mClassName.equals(mShowClassName))) {
            log.info(message);
        }
    }

    public void d(Object message){
        if((mLogType==LogType.ALL || mLogType==LogType.DEBUG) && ("".equals(mShowClassName) || mClassName.equals(mShowClassName))) {
            log.debug(message);
        }
    }

    /**
     * 设置显示类型
     * @param type log显示的级别
     */
    public static void setLogType(LogType type){
        mLogType = type;
    }

    public static void setShowClass(String className) {
        mShowClassName = className;
    }

    /**
     * log显示级别枚举类
     */
    public static enum LogType{
        OFF, ERROR, WARN, INFO, DEBUG, ALL
    }
}
