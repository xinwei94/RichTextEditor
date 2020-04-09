package com.xinwei.lib_richtext;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.xinwei.lib_richtext.interfaces.IEditorJsLisenter;
import com.xinwei.lib_richtext.utils.EditorUtils;

import java.io.File;

/**
 * Created by xinwei2 on 2020/3/5
 */

public class EditorJsInterface {

    private static final String TAG = "EditorJsInterface";

    private IEditorJsLisenter mLisenter;

    public EditorJsInterface(IEditorJsLisenter lisenter) {
        mLisenter = lisenter;
    }

    @JavascriptInterface
    public void clickText(String json) {
        Log.d(TAG, "clickText() json = " + json);
        if (null != mLisenter) {
            mLisenter.clickText(json);
        }
    }

    @JavascriptInterface
    public void clickImage(String json) {
        Log.d(TAG, "clickImage() json = " + json);
        if (null != mLisenter) {
            mLisenter.clickImage(json);
        }
    }

    @JavascriptInterface
    public void clickImageText(String json) {
        Log.d(TAG, "clickImageText() json = " + json);
        if (null != mLisenter) {
            mLisenter.clickImageText(json);
        }
    }

    @JavascriptInterface
    public void clickMoreBtnItem(String itemJson, String json) {
        Log.d(TAG, "clickMoreBtnItem() itemJson = " + itemJson + ", json = " + json);
        if (null != mLisenter) {
            mLisenter.clickMoreBtnItem(itemJson, json);
        }
    }

    @JavascriptInterface
    public void clickSpeaker(String json) {
        Log.d(TAG, "clickSpeaker() json = " + json);
        if (null != mLisenter) {
            mLisenter.clickSpeaker(json);
        }
    }

    @JavascriptInterface
    public void updateContent(String json) {
        Log.d(TAG, "updateContent() json = " + json);
        if (null != mLisenter) {
            mLisenter.updateContent(json);
        }
    }

    @JavascriptInterface
    public void saveDocFileContent(String content) {
        Log.d(TAG, "saveDocFileContent() content = " + content);
        if (null != mLisenter) {
            mLisenter.saveDocFileContent(content);
        }
    }

    @JavascriptInterface
    public String getImageBase64Data(String src) {
        if (TextUtils.isEmpty(src)) {
            return null;
        }

        if (src.startsWith("file:///")) {
            src = src.replace("file:///", "");
        }

        return EditorUtils.imageToBase64(src);
    }
}
