package com.lhx.hotfix.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.URLUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lhx.hotfix.activity.SecondActivity;

public class DownloadService extends IntentService {


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String urlToDownload = intent.getStringExtra("url");
        String name = URLUtil.guessFileName(urlToDownload, null, null);
        HttpURLConnection connection;
        try {
            URL url = new URL(urlToDownload);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();
            Log.d("test", "fileLength:" + fileLength);
            // download the file
            InputStream input = connection.getInputStream();
            OutputStream output = new FileOutputStream("/sdcard/" + name);
            byte data[] = new byte[2048];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                if (total * 100 / fileLength == 100) {
                    Intent finishIntent = new Intent(SecondActivity.ACTION_DOWNLOAD_SUCCESS);
                    finishIntent.putExtra("filePath", "/sdcard/" + name);
                    finishIntent.putExtra("fileName", name);
                    sendBroadcast(finishIntent);
                }
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
