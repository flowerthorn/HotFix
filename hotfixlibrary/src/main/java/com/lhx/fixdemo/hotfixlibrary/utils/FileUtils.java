package com.lhx.fixdemo.hotfixlibrary.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtils {

    /**
     * 复制单个文件
     *
     * @param sourcePath 原文件路径
     * @param targetPath 目标路径
     */
    public static void copyFile(String sourcePath, String targetPath) {
        try {
            int byteSum = 0;
            int byteRead;
            File targetFile = new File(targetPath);
            File sourceFile = new File(sourcePath);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            if (sourceFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(sourcePath); //读入原文件
                FileOutputStream fs = new FileOutputStream(targetPath);
                byte[] buffer = new byte[1024 * 5];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; //字节数 文件大小
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
}
