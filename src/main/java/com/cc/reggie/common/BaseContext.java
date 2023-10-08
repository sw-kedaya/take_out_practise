package com.cc.reggie.common;

/**
 * 封装ThreadLocal的工具类，获取当前用户id
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentUserId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentUserId(){
        return threadLocal.get();
    }
}
