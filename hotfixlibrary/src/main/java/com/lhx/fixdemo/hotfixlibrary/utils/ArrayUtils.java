package com.lhx.fixdemo.hotfixlibrary.utils;

import java.lang.reflect.Array;

public class ArrayUtils {

    public static Object combineArray(Object arrayPre, Object arrayLast) {
        //获得一个数组的class对象，通过Array.newInstance()可以反射生成数组对象
        Class<?> localClass = arrayPre.getClass().getComponentType();
        //前数组长度
        int preLength = Array.getLength(arrayPre);
        //重新生成的数组长度
        int newLength = preLength + Array.getLength(arrayLast);
        //生成数组对象
        Object result = Array.newInstance(localClass, newLength);
        for (int i = 0; i < newLength; ++i) {
            if (i < preLength) {
                //从0开始遍历 如果前数组有值 添加到新数组的第一个位置
                Array.set(result, i, Array.get(arrayPre, i));
            } else {
                //添加完前数组 再添加后数组 合并完成
                Array.set(result, i, Array.get(arrayLast, i - preLength));
            }
        }
        return result;
    }
}