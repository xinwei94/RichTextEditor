package com.xinwei.richtexteditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
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
import com.xinwei.lib_richtext.interfaces.IEditorSaveDocLisenter;
import com.xinwei.richtexteditor.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xinwei2 on 2020/3/5
 */
public class RichTextEditAcivity extends Activity implements View.OnClickListener {

    private static final String TAG = "RichTextEditAcivity";

    private TextView mTitleTextView;

    private RichTextEditor mEditor;

    private Button mPlayBtn;
    private Button mEditBtn;
    private Button mSaveBtn;
    private Button mCancelBtn;
    private Button mSaveDocBtn;
    private Button mFontSizeBtn;

    private View mEditInsertPart;
    private Button mInsertTextBtn;
    private Button mInsertImageBtn;
    private Button mInsertmageTextBtn;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private double mCurrentPositon;

    //判断文本双击
    private double mTextStartTime;
    private long mLastClickTextTime;

    private int mIndex;
    private double mStartTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_text_edit);

        initView();
        initData();
    }

    private void initView() {
        mEditor = (RichTextEditor) findViewById(R.id.rich_text_editor);
        mEditor.setLisenter(mEditorLisenter);

        mTitleTextView = findViewById(R.id.title_text);
        mPlayBtn = findViewById(R.id.btn_play);
        mEditBtn = findViewById(R.id.btn_edit);
        mSaveBtn = findViewById(R.id.btn_save);
        mCancelBtn = findViewById(R.id.btn_cancel);
        mSaveDocBtn = findViewById(R.id.btn_doc);
        mFontSizeBtn = findViewById(R.id.btn_font_size);

        mEditInsertPart = findViewById(R.id.edit_insert_part);
        mInsertTextBtn = findViewById(R.id.btn_insert_text);
        mInsertImageBtn = findViewById(R.id.btn_insert_image);
        mInsertmageTextBtn = findViewById(R.id.btn_insert_image_text);

        mPlayBtn.setOnClickListener(this);
        mEditBtn.setOnClickListener(this);
        mSaveBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mSaveDocBtn.setOnClickListener(this);
        mFontSizeBtn.setOnClickListener(this);
        mInsertTextBtn.setOnClickListener(this);
        mInsertImageBtn.setOnClickListener(this);
        mInsertmageTextBtn.setOnClickListener(this);
    }

    private void initData() {
        mEditor.setContent(getRichTextData());

        List<MoreBtnItemInfo> moreBtnItemInfos = new ArrayList<>();
        moreBtnItemInfos.add(new MoreBtnItemInfo(1, "查看图片"));
        moreBtnItemInfos.add(new MoreBtnItemInfo(2, "文字识别"));
        mEditor.setMoreBtn(moreBtnItemInfos);
    }

    private void startPlay() {
        //模拟播放器更新播放进度
        mCurrentPositon = 0;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                mCurrentPositon += 1.1f;
                mCurrentPositon = (double) Math.round(mCurrentPositon * 100) / 100;
                Log.d(TAG, "currentPositon = " + mCurrentPositon);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEditor.updateLightProgress(mCurrentPositon);
                    }
                });

            }
        };
        mTimer.schedule(mTimerTask, 0, 1100);
    }

    private void stopPlay() {
        if (null != mTimer) {
            mTimer.cancel();
        }
        mCurrentPositon = 0;
    }

    private boolean isPlaying() {
        return 0 != mCurrentPositon;
    }

    private void insertText() {
        double startTime = mStartTime;
        double endTime = startTime + 1;
        String content = "新插入的一段文本，";
        mEditor.insertText(mIndex, startTime, endTime, content);
    }

    private void insertImage() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "image2.jpg";
        mEditor.insertImage(mIndex, mStartTime, path);
    }

    private void insertImageWithText() {
        double startTime = mStartTime;
        String path = Environment.getExternalStorageDirectory() + File.separator + "image3.jpg";
        String content = "图片识别结果：新插入的一张图片";
        mEditor.insertImageWithText(mIndex, startTime, path, content);
    }

    private void showToast(String text) {
        Toast.makeText(RichTextEditAcivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private void changeEditBtnState(boolean isEdit) {
        mEditBtn.setVisibility(!isEdit ? View.VISIBLE : View.GONE);
        mPlayBtn.setVisibility(!isEdit ? View.VISIBLE : View.GONE);
        mSaveDocBtn.setVisibility(!isEdit ? View.VISIBLE : View.GONE);
        mFontSizeBtn.setVisibility(!isEdit ? View.VISIBLE : View.GONE);
        mSaveBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        mCancelBtn.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        mEditInsertPart.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        mTitleTextView.setText(isEdit ? "编辑文档" : "浏览文档");
    }

    public void finishPage(View view) {
        finish();
    }

    private RichTextData getRichTextData() {
//        List<BaseRichTextInfo> textRichInfos = new ArrayList<>();
//        for (int i = 1; i <= 50; i++) {
//            TextRichInfo richInfo = new TextRichInfo();
//            richInfo.setIndex(i);
//            richInfo.setContent("这是第" + i + "句输入的文本，凑几个字看看。");
//            richInfo.setStartTime(i + 0.1);
//            richInfo.setEndTime(i  + 1.1);
//            textRichInfos.add(richInfo);
//        }
//
//        //插入图片
//        String path = Environment.getExternalStorageDirectory() + File.separator + "image2.jpg";
//        ImageRichInfo imageRichInfo = new ImageRichInfo();
//        imageRichInfo.setImageUrl(path);
//        textRichInfos.add(4, imageRichInfo);
//
//        return new RichTextData(textRichInfos);

        return RichTextSaveHelper.getRichTextData();
    }


    private void saveDoc() {

        mEditor.saveDocFile(new IEditorSaveDocLisenter() {
            @Override
            public void onSaveContent(String content) {
                FileUtils.writeString(RichTextSaveHelper.DOC_FILE_PATH, content, true);
                showToast("Doc文档已保存到" + RichTextSaveHelper.DOC_FILE_PATH);
            }
        });
    }

    private void showSpeaker(boolean isShow) {
        mEditor.setShowSpeaker(isShow);
    }

    private void showRenameDialog(final SpeakerInfo speakerInfo) {
        final EditText editText = new EditText(RichTextEditAcivity.this);
        editText.setText(speakerInfo.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(RichTextEditAcivity.this);
        builder.setTitle("发言人重命名");
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "onClickPositive() " + Thread.currentThread().getName());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onClickPositive() run " + Thread.currentThread().getName());
                        String name = editText.getText().toString();
                        mEditor.renameSpeaker(speakerInfo.getName(), name);
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void showFontSizeDialog() {
        final SeekBar seekBar = new SeekBar(RichTextEditAcivity.this);
        seekBar.setMax(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mEditor.setFontSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(RichTextEditAcivity.this);
        builder.setTitle("调节字体");
        builder.setView(seekBar);
        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
    }

    private IEditorLisenter mEditorLisenter = new IEditorLisenter() {
        @Override
        public void onInitFinish() {
            Log.d(TAG, "onClickText = " + mCurrentPositon);
        }

        @Override
        public void onClickText(TextRichInfo info) {
            Log.d(TAG, "onClickText() text = " + info.getContent());
            mIndex = info.getIndex();
            mStartTime = info.getStartTime();

            if (!isPlaying()) {
                return;
            }

            long currentTime = System.currentTimeMillis();
            if ((Math.abs(currentTime - mLastClickTextTime) < 1000)
                    && (mTextStartTime == info.getStartTime())) {
                //双击事件
                mCurrentPositon = info.getStartTime() - 1.1f;
            }
            mTextStartTime = info.getStartTime();
            mLastClickTextTime = currentTime;
        }

        @Override
        public void onClickImage(ImageRichInfo info) {
            showToast("点击图片：" + info.getImageUrl());
        }

        @Override
        public void onClickImageText(ImageTextRichInfo info) {
            showToast("点击图文框：" + info.getContent());
        }

        @Override
        public void onClickMoreBtnItem(MoreBtnItemInfo itemInfo, BaseRichTextInfo richTextInfo) {
            showToast("点击更多条目：" + itemInfo.getText());
        }

        @Override
        public void onClickSpeaker(SpeakerInfo speakerInfo) {
            showRenameDialog(speakerInfo);
        }

        @Override
        public void updateContent(RichTextData richTextData) {
            showToast("保存成功");
            RichTextSaveHelper.setRichTextData(richTextData);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play:
                if (isPlaying()) {
                    stopPlay();
                    mPlayBtn.setText("播放");
                } else {
                    startPlay();
                    mPlayBtn.setText("暂停");
                }
                break;
            case R.id.btn_edit:
                changeEditBtnState(true);
                mEditor.editContent();
                break;
            case R.id.btn_save:
                changeEditBtnState(false);
                mEditor.saveEdit();
                break;
            case R.id.btn_cancel:
                changeEditBtnState(false);
                mEditor.cancelEdit();
                break;
            case R.id.btn_doc:
                saveDoc();
                break;
            case R.id.btn_insert_text:
                insertText();
                break;
            case R.id.btn_insert_image:
                insertImage();
                break;
            case R.id.btn_insert_image_text:
                insertImageWithText();
                break;
            case R.id.btn_font_size:
                showFontSizeDialog();
                break;
            default:
                break;
        }
    }
}
