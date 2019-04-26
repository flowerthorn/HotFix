package com.lhx.fixdemo.hotfixlibrary.utils;

import java.lang.reflect.Field;

public class ReflectUtils {

    /**
     * 通过反射获取某对象 并设置私有可访问
     *
     * @param obj   该属性所属类的对象
     * @param clazz 该属性所属类
     * @param field 属性名
     * @return 该属性对象
     */
    private static Object getField(Object obj, Class<?> clazz, String field)
            throws NoSuchFieldException, IllegalAccessException {
        Field localField = clazz.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    /**
     * 为某属性赋值，并设置私有可访问
     *
     * @param obj   该属性所属类的对象
     * @param clazz 该属性所属的类
     * @param value 值
     */
    public static void setField(Object obj, Class<?> clazz, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field localFiled = clazz.getDeclaredField("dexElements");
        localFiled.setAccessible(true);
        localFiled.set(obj, value);
    }

    /**
     * 通过反射获取BaseDexClassLoader对象中的PathList对象
     *
     * @param baseDexClassLoader BaseClassLoader
     * @return PathList对象
     */
    public static Object getPathList(Object baseDexClassLoader)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"),
                "pathList");
    }

    /**
     * 通过反射获取BaseDexClassLoader对象中的PathList对象
     *
     * @param pathListObject PathList
     * @return PathList对象
     */
    public static Object getDexElements(Object pathListObject)
            throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return getField(pathListObject, pathListObject.getClass(), "dexElements");
    }

}
