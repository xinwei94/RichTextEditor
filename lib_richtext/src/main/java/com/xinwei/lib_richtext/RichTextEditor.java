package com.xinwei.lib_richtext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.xinwei.lib_richtext.entities.BaseRichTextInfo;
import com.xinwei.lib_richtext.entities.ImageRichInfo;
import com.xinwei.lib_richtext.entities.ImageTextRichInfo;
import com.xinwei.lib_richtext.entities.MoreBtnItemInfo;
import com.xinwei.lib_richtext.entities.RichTextData;
import com.xinwei.lib_richtext.entities.SpeakerInfo;
import com.xinwei.lib_richtext.entities.TextRichInfo;
import com.xinwei.lib_richtext.interfaces.IEditorJsLisenter;
import com.xinwei.lib_richtext.interfaces.IEditorLisenter;
import com.xinwei.lib_richtext.interfaces.IEditorSaveDocLisenter;
import com.xinwei.lib_richtext.utils.EditorUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.List;

/**
 * 富文本编辑器
 * Created by xinwei2 on 2020/3/5
 */

public class RichTextEditor extends WebView {

    private static final String TAG = "RichTextEditor";

    private static final String BASE_HTML = "file:///android_asset/richtext_editor.html";

    private boolean mIsReady;

    private IEditorLisenter mLisenter;
    private IEditorSaveDocLisenter mSaveDocLisenter;

    public RichTextEditor(Context context) {
        this(context, null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    /**
     * 设置监听
     *
     * @param lisenter 监听
     */
    public void setLisenter(IEditorLisenter lisenter) {
        mLisenter = lisenter;
    }

    /**
     * 设置富文本内容
     *
     * @param richTextData 内容列表
     */
    public void setContent(RichTextData richTextData) {
        String richDataJson = new Gson().toJson(richTextData);
        Log.d(TAG, "setContent() richDataJson = " + richDataJson);

        exec("javascript:setContent('" + richDataJson + "');");
    }

    /**
     * 更新高亮位置
     *
     * @param second 当前时间（秒）
     */
    public void updateLightProgress(double second) {
        Log.d(TAG, "updateLightProgress() second = " + second);
        exec("javascript:updateProgress('" + second + "');");
    }

    /**
     * 编辑文档
     */
    public void editContent() {
        Log.d(TAG, "editContent()");
        exec("javascript:editContent();");
    }

    /**
     * 保存编辑
     */
    public void saveEdit() {
        Log.d(TAG, "saveEdit()");
        exec("javascript:saveEdit();");
    }

    /**
     * 取消编辑
     */
    public void cancelEdit() {
        Log.d(TAG, "cancelEdit()");
        exec("javascript:cancelEdit();");
    }

    /**
     * 添加文本
     *
     * @param index     文本序号
     * @param startTime 文本起始时间
     * @param endTime   文本结束时间
     * @param content   文本内容
     */
    public void addText(int index, double startTime, double endTime, String content) {
        addText(index, startTime, endTime, content, null, null);
    }

    /**
     * 添加文本
     *
     * @param index       文本序号
     * @param startTime   文本起始时间
     * @param endTime     文本结束时间
     * @param content     文本内容
     * @param speakerId   发言人ID
     * @param speakerName 发言人名称
     */
    public void addText(int index, double startTime, double endTime, String content, String speakerId, String speakerName) {
        Log.d(TAG, "addText() index = " + index + ", startTime = " + startTime + ", endTime = " + endTime
                + ", speakerId = " + speakerId + ", speakerName = " + speakerName + ", content = " + content);
        if (null == speakerId) speakerId = "";
        if (null == speakerName) speakerName = "";

        exec("javascript:addText('" + content + "','" + index + "','" + startTime + "','"
                + endTime + "','" + speakerId + "','" + speakerName + "', true);");
    }

    /**
     * 插入文本（根据startTime插入指定位置）
     *
     * @param index     文本序号
     * @param startTime 文本起始时间
     * @param endTime   文本结束时间
     * @param content   文本内容
     */
    public void insertText(int index, double startTime, double endTime, String content) {
        Log.d(TAG, "insertText() index = " + index + ", startTime = " + startTime
                + ", endTime = " + endTime + ", content = " + content);
        exec("javascript:insertText('" + content + "','" + index + "','" + startTime + "','" + endTime + "', true);");
    }

    /**
     * 添加图片
     *
     * @param index     图片序号
     * @param startTime 图片起始时间
     * @param path      图片路径
     */
    public void addImage(int index, double startTime, String path) {
        Log.d(TAG, "addImage() index = " + index + ", startTime = " + startTime + ", path = " + path);
        String url = "file:///" + path;
        exec("javascript:addImage('" + url + "','" + index + "','" + startTime + "', true);");
    }

    /**
     * 插入图片（根据startTime插入指定位置）
     *
     * @param index     图片序号
     * @param startTime 图片起始时间
     * @param path      图片路径
     */
    public void insertImage(int index, double startTime, String path) {
        Log.d(TAG, "insertImage() index = " + index + ", startTime = " + startTime + ", path = " + path);
        String url = "file:///" + path;
        exec("javascript:insertImage('" + url + "','" + index + "','" + startTime + "', true);");
    }

    /**
     * 添加图片+文字描述
     *
     * @param index     图片序号
     * @param startTime 图片起始时间
     * @param path      图片路径
     * @param content   图片描述
     */
    public void addImageWithText(int index, double startTime, String path, String content) {
        Log.d(TAG, "addImageWithText() index = " + index + ", startTime = " + startTime
                + ", path = " + path + ", content = " + content);
        String url = "file:///" + path;
        exec("javascript:addImageWithText('" + url + "','" + index + "','" + startTime + "','" + content + "', true);");
    }

    /**
     * 插入图片+文字描述（根据startTime插入指定位置）
     *
     * @param index     图片序号
     * @param startTime 图片起始时间
     * @param path      图片路径
     * @param content   图片描述
     */
    public void insertImageWithText(int index, double startTime, String path, String content) {
        Log.d(TAG, "insertImageWithText() index = " + index + ", startTime = " + startTime
                + ", path = " + path + ", content = " + content);
        String url = "file:///" + path;
        exec("javascript:insertImageWithText('" + url + "','" + index + "','" + startTime + "','" + content + "', true);");
    }

    /**
     * 插入临时的高亮文本
     *
     * @param content 高亮文本
     */
    public void insertTempLight(String content) {
        Log.d(TAG, "insertTempLight() content = " + content);
        exec("javascript:insertTempLight('" + content + "');");
    }

    /**
     * 移除临时的高亮文本
     */
    public void removeTempLight() {
        Log.d(TAG, "removeTempLight()");
        exec("javascript:removeTempLight();");
    }

    /**
     * 保存文档
     */
    public void saveContent() {
        Log.d(TAG, "saveContent()");
        exec("javascript:saveContent();");
    }

    /**
     * 保存为Doc文档
     *
     * @param lisenter 回调
     */
    public void saveDocFile(IEditorSaveDocLisenter lisenter) {
        Log.d(TAG, "saveDocFile() lisenter = " + lisenter);
        mSaveDocLisenter = lisenter;
        if (null == lisenter) {
            return;
        }

        exec("javascript:saveDocFile();");
    }

    /**
     * 设置自定义更多按钮
     *
     * @param moreBtnItemInfos 按钮条目信息
     */
    public void setMoreBtn(List<MoreBtnItemInfo> moreBtnItemInfos) {
        Log.d(TAG, "setMoreBtn() moreBtnItemInfos = " + moreBtnItemInfos);

        String moreBtnJson = new Gson().toJson(moreBtnItemInfos);
        Log.d(TAG, "setMoreBtn() moreBtnJson = " + moreBtnJson);

        exec("javascript:setMoreBtn('" + moreBtnJson + "');");
    }

    /**
     * 设置显示或隐藏发言人
     *
     * @param isShow 是否显示
     */
    public void setShowSpeaker(boolean isShow) {
        Log.d(TAG, "setShowSpeaker() isShow = " + isShow);
        exec("javascript:setShowSpeaker(" + isShow + ");");
    }

    /**
     * 发言人重命名
     *
     * @param oldname 原名称
     * @param newname 新名称
     */
    public void renameSpeaker(String oldname, String newname) {
        Log.d(TAG, "renameSpeaker() oldname = " + oldname + ", newname = " + newname);
        exec("javascript:ranameSpeaker('" + oldname + "','" + newname + "');");
    }

    /**
     * 设置发言人颜色
     *
     * @param colorArray 色值
     */
    public void setSpeakerColor(String[] colorArray) {
        if (null == colorArray || 0 == colorArray.length) {
            Log.d(TAG, "setSpeakerColor() colorArray is null, do nothing");
            return;
        }

        String colorStr = "[";
        for (String color : colorArray) {
            colorStr += ("'" + color + "',");
        }
        colorStr = colorStr.substring(0, colorStr.length() - 1);
        colorStr += "]";
        Log.d(TAG, "setSpeakerColor() colorStr = " + colorStr);

        exec("javascript:setSpeakerColor(" + colorStr + ");");
    }

    /**
     * 设置字体大小
     *
     * @param size 字体大小
     */
    public void setFontSize(int size) {
        Log.d(TAG, "setFontSize() size = " + size);
        exec("javascript:setFontSize(" + size + ");");
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        //隐藏滚动条
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);

        //浏览器事件回调
        setWebViewClient(createWebviewClient());
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                super.onConsoleMessage(message, lineNumber, sourceID);
                Log.d(TAG, "initView() message = " + message + ", lineNumber = " + lineNumber + ", sourceID = " + sourceID);
            }
        });

        //开启JS通信能力
        getSettings().setJavaScriptEnabled(true);
        addJavascriptInterface(new EditorJsInterface(mEditorJsLisenter), "jsInterface");

        loadUrl(BASE_HTML);
    }


    private void exec(final String script) {
        if (mIsReady) {
            load(script);
        } else {
            Log.d(TAG, "exec() mIsReady = false, delay");
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(script);
                }
            }, 100);
        }
    }

    private void load(String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(script, null);//不会使页面刷新（loadUrl会导致刷新）
        } else {
            loadUrl(script);
        }
    }

    private void onPageFinished() {
        Log.d(TAG, "onPageFinished()");
        mIsReady = true;
        notifyInitFinish();
    }

    private EditWebViewClient createWebviewClient() {
        return new EditWebViewClient(new WeakReference<RichTextEditor>(this));
    }

    private void notifyInitFinish() {
        if (null != mLisenter) {
            mLisenter.onInitFinish();
        }
    }

    private void notifyClickText(TextRichInfo textRichInfo) {
        if (null != mLisenter) {
            mLisenter.onClickText(textRichInfo);
        }
    }

    private void notifyClickImage(ImageRichInfo imageRichInfo) {
        if (null != mLisenter) {
            mLisenter.onClickImage(imageRichInfo);
        }
    }

    private void notifyClickImageText(ImageTextRichInfo imageTextRichInfo) {
        if (null != mLisenter) {
            mLisenter.onClickImageText(imageTextRichInfo);
        }
    }

    private void notifyClickMoreBtnItem(MoreBtnItemInfo moreBtnItemInfo, BaseRichTextInfo richTextInfo) {
        if (null != mLisenter) {
            mLisenter.onClickMoreBtnItem(moreBtnItemInfo, richTextInfo);
        }
    }

    private void notifyClickSpeaker(SpeakerInfo speakerInfo) {
        if (null != mLisenter) {
            mLisenter.onClickSpeaker(speakerInfo);
        }
    }

    private void notifyUpdateContent(RichTextData richTextData) {
        if (null != mLisenter) {
            mLisenter.updateContent(richTextData);
        }
    }

    private void notifySaveDocContent(String content) {
        if (null != mSaveDocLisenter) {
            mSaveDocLisenter.onSaveContent(content);
        }
    }

    private IEditorJsLisenter mEditorJsLisenter = new IEditorJsLisenter() {

        @Override
        public void clickText(String json) {
            notifyClickText(EditorUtils.parseTextRichInfo(json));
        }

        @Override
        public void clickImage(String json) {
            notifyClickImage(EditorUtils.parseImageRichInfo(json));
        }

        @Override
        public void clickImageText(String json) {
            notifyClickImageText(EditorUtils.parseImageTextRichInfo(json));
        }

        @Override
        public void clickMoreBtnItem(String itemJson, String json) {
            notifyClickMoreBtnItem(EditorUtils.parseMoreBtnItemInfo(itemJson), EditorUtils.parseBaseRichTextInfo(json));
        }

        @Override
        public void clickSpeaker(String json) {
            notifyClickSpeaker(EditorUtils.parseSpeakerInfo(json));
        }

        @Override
        public void updateContent(String json) {
            notifyUpdateContent(EditorUtils.parseRichTextData(json));
        }

        @Override
        public void saveDocFileContent(String content) {
            notifySaveDocContent(content);
        }
    };

    private static class EditWebViewClient extends WebViewClient {

        private WeakReference<RichTextEditor> weakReference;

        public EditWebViewClient(WeakReference<RichTextEditor> reference) {
            weakReference = reference;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            boolean isReady = url.equalsIgnoreCase(BASE_HTML);
            if (null != weakReference && isReady) {
                weakReference.get().onPageFinished();
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
