package com.xinwei.lib_richtext.entities;

/**
 * 纯文本
 * Created by xinwei2 on 2020/3/5
 */

public class TextRichInfo extends BaseRichTextInfo {

    private SpeakerInfo speakerInfo;

    public TextRichInfo() {
        setType(RichTextType.TYPE_TEXT);
    }

    public SpeakerInfo getSpeakerInfo() {
        return speakerInfo;
    }

    public void setSpeakerInfo(SpeakerInfo speakerInfo) {
        this.speakerInfo = speakerInfo;
    }
}
