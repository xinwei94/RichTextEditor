package com.xinwei.lib_richtext.interfaces;

/**
 * Created by xinwei2 on 2020/3/5
 */

public interface IEditorJsLisenter {

    void clickText(String json);

    void clickImage(String json);

    void clickImageText(String json);

    void clickMoreBtnItem(String itemJson, String json);

    void clickSpeaker(String json);

    void updateContent(String json);

    void saveDocFileContent(String content);
}
