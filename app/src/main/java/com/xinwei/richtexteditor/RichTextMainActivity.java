package com.xinwei.richtexteditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;


import com.xinwei.richtexteditor.utils.AssetUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by xinwei2 on 2020/3/13
 */

public class RichTextMainActivity extends Activity {

    private static final String TAG = "RichTextMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_main);

        initData();
    }

    private void initData() {
        copyImage();
    }

    public void goRichTextCreate(View view) {
        startActivity(new Intent(this, RichTextCreateActivity.class));
    }

    public void goRichTextEidt(View view) {
        startActivity(new Intent(this, RichTextEditAcivity.class));
    }

    public void finishPage(View view) {
        finish();
    }

    private void copyImage() {
        Log.d(TAG, "copyImage()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory() + File.separator;
                String[] pathList = new String[] {"image1.jpg", "image2.jpg", "image3.jpg"};
                for (int i = 0; i < pathList.length; i++) {
                    String imagePath = path + pathList[i];
                    Log.d(TAG, "copyImage() imagePath = " + imagePath);
                    if (!new File(imagePath).exists()) {
                        Log.d(TAG, "imagePath() copy image to sdcard, imagePath = " + imagePath);
                        try {
                            AssetUtils.copy(RichTextMainActivity.this, pathList[i], imagePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(TAG, "imagePath() image exists, do nothing");
                    }
                }
            }
        }).start();
    }
}
