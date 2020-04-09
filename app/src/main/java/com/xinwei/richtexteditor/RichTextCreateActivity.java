package com.xinwei.richtexteditor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.xinwei.lib_richtext.RichTextEditor;
import com.xinwei.lib_richtext.entities.BaseRichTextInfo;
import com.xinwei.lib_richtext.entities.ImageRichInfo;
import com.xinwei.lib_richtext.entities.ImageTextRichInfo;
import com.xinwei.lib_richtext.entities.MoreBtnItemInfo;
import com.xinwei.lib_richtext.entities.RichTextData;
import com.xinwei.lib_richtext.entities.SpeakerInfo;
import com.xinwei.lib_richtext.entities.TextRichInfo;
import com.xinwei.lib_richtext.interfaces.IEditorLisenter;

import java.io.File;

/**
 * Created by xinwei2 on 2020/3/6
 */
public class RichTextCreateActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RichTextCreateActivity";

    private RichTextEditor mEditor;

    private Button mSaveBtn;
    private Button mTextBtn;
    private Button mLightTextBtn;
    private Button mImageBtn;
    private Button mImageTextBtn;
    private TextView mTextCountTextView;
    private TextView mImageCountTextView;

    private int mIndex;
    private int mSpeakerIndex;

    private String[] mContentArray;

    //统计字数和图片数
    private int mTextCount;
    private int mImageCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_create);

        initView();
        initData();
    }

    private void initView() {
        mEditor = (RichTextEditor) findViewById(R.id.rich_text_editor);
        mEditor.setLisenter(mEditorLisenter);

        mSaveBtn = findViewById(R.id.btn_save);
        mTextBtn = findViewById(R.id.btn_text);
        mLightTextBtn = findViewById(R.id.btn_light_text);
        mImageBtn = findViewById(R.id.btn_image);
        mImageTextBtn = findViewById(R.id.btn_image_text);
        mTextCountTextView = findViewById(R.id.text_count_view);
        mImageCountTextView = findViewById(R.id.image_count_view);

        mSaveBtn.setOnClickListener(this);
        mTextBtn.setOnClickListener(this);
        mLightTextBtn.setOnClickListener(this);
        mImageBtn.setOnClickListener(this);
        mImageTextBtn.setOnClickListener(this);
    }

    private void initData() {
        mContentArray = getResources().getStringArray(R.array.test_content);
    }

    private void addText() {
        removeTempLight();

        double startTime = mIndex + 0.1;
        double endTime = startTime + 1;
        String content = mContentArray[mIndex % mContentArray.length];
        String speaker = "发言人" + (mSpeakerIndex++ / 10) + ":";
        if (mSpeakerIndex >= 50) mSpeakerIndex = 0;

        mEditor.addText(mIndex, startTime, endTime, content, speaker);

        mIndex++;
        refreshShowCount(content.length(), 0);
    }

    private void addImage() {
        double startTime = mIndex + 0.1;
        String path = Environment.getExternalStorageDirectory() + File.separator + "image1.jpg";
        mEditor.addImage(mIndex, startTime, path);

        refreshShowCount(0, 1);
    }

    private void addImageWithText() {
        double startTime = mIndex + 0.1;
        String path = Environment.getExternalStorageDirectory() + File.separator + "image3.jpg";
        String content = "图片识别结果：别梦依依到谢家，小廊回合曲阑斜。多情只有春庭月，犹为离人照落花。";
        mEditor.addImageWithText(mIndex, startTime, path, content);

        refreshShowCount(content.length(), 1);
    }

    private void insertTempLight() {
        String content = mContentArray[(int) (System.currentTimeMillis() % mContentArray.length)];
        mEditor.insertTempLight(content);
    }

    private void saveContent() {
        mEditor.saveContent();
    }

    private void removeTempLight() {
        mEditor.removeTempLight();
    }

    private void refreshShowCount(int addTextCount, int addImageCount) {
        mTextCount += addTextCount;
        mImageCount += addImageCount;

        mTextCountTextView.setText("文字数：" + mTextCount);
        mImageCountTextView.setText("图片数：" + mImageCount);
    }

    private void showToast(String text) {
        Toast.makeText(RichTextCreateActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    public void finishPage(View view) {
        finish();
    }

    private IEditorLisenter mEditorLisenter = new IEditorLisenter() {
        @Override
        public void onInitFinish() {
            Log.d(TAG, "onInitFinish()");
        }

        @Override
        public void onClickText(TextRichInfo info) {

        }

        @Override
        public void onClickImage(ImageRichInfo info) {

        }

        @Override
        public void onClickImageText(ImageTextRichInfo info) {

        }

        @Override
        public void onClickMoreBtnItem(MoreBtnItemInfo itemInfo, BaseRichTextInfo richTextInfo) {

        }

        @Override
        public void onClickSpeaker(SpeakerInfo speakerInfo) {

        }

        @Override
        public void updateContent(RichTextData richTextData) {
            Log.d(TAG, "updateContent() richTextData = " + richTextData);
            RichTextSaveHelper.setRichTextData(richTextData);

            showToast("保存成功");
            finishPage(null);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveContent();
                break;
            case R.id.btn_text:
                addText();
                break;
            case R.id.btn_light_text:
                insertTempLight();
                break;
            case R.id.btn_image:
                addImage();
                break;
            case R.id.btn_image_text:
                addImageWithText();
                break;
            default:
                break;
        }
    }
}
