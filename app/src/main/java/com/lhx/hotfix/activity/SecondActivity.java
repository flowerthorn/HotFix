package com.lhx.hotfix.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;

import com.lhx.fixdemo.hotfixlibrary.FixDexUtils;
import com.lhx.fixdemo.hotfixlibrary.utils.FileUtils;
import com.lhx.hotfix.CalculateUtils;
import com.lhx.hotfix.R;
import com.lhx.hotfix.base.BaseActivity;
import com.lhx.hotfix.service.DownloadService;

public class SecondActivity extends BaseActivity {


    private TextView tvResult;

    private Button btnCalculate;

    private Button btnFix;

    private DownLoadFileBroadCastReceiver myReceiver;

    public static final String ACTION_DOWNLOAD_SUCCESS
            = "com.lhx.hotfix.activity.SecondActivity.Download.Success";

    public static final String URL_BUG_PATCH =
            "http://lc-dfilqc4v.cn-n1.lcfile.com/d1e93d38e420e20b91e2/classes2.dex";

    public static void start(Context context) {
        context.startActivity(new Intent(context, SecondActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tvResult = findViewById(R.id.result_tv);
        btnCalculate = findViewById(R.id.calculate_btn);
        btnFix = findViewById(R.id.fix_btn);
        btnFix.setOnClickListener(v -> fixBug());
        btnCalculate.setOnClickListener(v -> calculate());
        myReceiver = new DownLoadFileBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOWNLOAD_SUCCESS);
        registerReceiver(myReceiver, intentFilter);
    }

    private void calculate() {
        tvResult.setText("结果为" + CalculateUtils.math() + "");
    }

    private void fixBug() {
        //网络请求 应该
        //去服务端接口请求（判断该版本是否有bug patch 如果没有不需要下载 有的话下载）
        //本demo就不提供接口 直接给了一个服务端patch文件的地址 直接下载了。默认需要修复。模拟有bug
        Intent downloadIntent = new Intent(this, DownloadService.class);
        downloadIntent.putExtra("url", URL_BUG_PATCH);
        startService(downloadIntent);
    }

    private class DownLoadFileBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_DOWNLOAD_SUCCESS)) {
                Log.d("Log.SecondActivity", "下载完成,复制修复包dex文件到app的私有目录...");
                String originFilePath = intent.getStringExtra("filePath");
                String targetFilePath = getDir("odex", Context.MODE_PRIVATE)
                        .getAbsolutePath() + File.separator + intent
                        .getStringExtra("fileName");//私有目录的odex文件夹下
                Log.d("Log.SecondActivity",
                        "originFilePath:" + originFilePath + "targetFilePath：" + targetFilePath);
                //下载完成 下载路径为/sdcard/class2.dex
                //首先要把sd卡的class2.dex文件复制到app私有目录下
                FileUtils.copyFile(originFilePath, targetFilePath);
                FixDexUtils.loadFixedDex(SecondActivity.this);//真正开始修复的地方
            }
        }
    }
}
