package com.lhx.fixdemo.hotfixlibrary;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashSet;

import com.lhx.fixdemo.hotfixlibrary.utils.ArrayUtils;
import com.lhx.fixdemo.hotfixlibrary.utils.ReflectUtils;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixDexUtils {

    private static HashSet<File> loadedDex = new HashSet<>();

    //加载已修复的dex
    public static void loadFixedDex(@NonNull Context context) {
        if (context == null) {
            return;
        }
        //1. dex文件目录
        File fileDir = context.getDir("odex", Context.MODE_PRIVATE);
        //遍历私有目录下的所有文件
        File[] files = fileDir.listFiles();
        for (File file : files) {
            //找到修复包
            if (file.getName().endsWith(".dex") && !"classes.dex".equals(file.getName())) {
                loadedDex.add(file);
            }
        }
        //2.  创建我们自有的类加载器
        createDexClassLoader(context, fileDir);
    }

    /**
     * 创建加载补丁的dexclassloader
     *
     * @param context 上下文
     * @param fileDir dex文件目录
     */
    private static void createDexClassLoader(Context context, File fileDir) {
        //创建临时的解压目录 用来存放dex-java文件(先解压到该目录，再加载java)
        String optimizedDir = fileDir.getAbsoluteFile() + File.separator + "opt_dex";
        File optimizedFile = new File(optimizedDir);
        if (!optimizedFile.exists()) {
            optimizedFile.mkdirs();
        }
        //创建类加载器
        for (File dex : loadedDex) {
            //每遍历一个要修复的dex文件，就要插桩一次 hook点
            DexClassLoader classLoader = new DexClassLoader(dex.getAbsolutePath(), optimizedDir,
                    null, context.getClassLoader());
            hotFix(classLoader, context);
        }
    }

    /**
     * 热修复
     *
     * @param classLoader 自己创建的类加载器，加载了修复包的DexClassLoader
     * @param context     上下文
     */
    private static void hotFix(DexClassLoader classLoader, Context context) {
        //关键
        //获取系统的类加载器PathClassLoader
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            //反射获取自有的dexElements数组对象
            Object mDexElements = ReflectUtils
                    .getDexElements(ReflectUtils.getPathList(classLoader));
            //反射获取系统的pathList和dexElements数组对象
            Object systemPathList = ReflectUtils.getPathList(pathClassLoader);
            Object systemDexElements = ReflectUtils
                    .getDexElements(systemPathList);
            //合并成新的dexElements数组对象
            Object combineDexElements = ArrayUtils.combineArray(mDexElements, systemDexElements);
            //重新赋值给系统的pathList属性--修改pathList中的dexElements数组对象
            ReflectUtils.setField(systemPathList, systemPathList.getClass(), combineDexElements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
