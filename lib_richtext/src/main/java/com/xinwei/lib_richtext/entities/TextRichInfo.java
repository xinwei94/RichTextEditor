package com.xinwei.lib_richtext.entities;

/**
 * 纯文本
 * Created by xinwei2 on 2020/3/5
 */

public class TextRichInfo extends BaseRichTextInfo {

    private String speaker;

    public TextRichInfo() {
        setType(RichTextType.TYPE_TEXT);
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }
}
