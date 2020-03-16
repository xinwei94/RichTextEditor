package com.xinwei.lib_richtext.entities;

/**
 * 富文本信息基类
 * Created by xinwei2 on 2020/3/5
 */

public class BaseRichTextInfo {

    private int type = RichTextType.TYPE_TEXT;

    private int index;

    private double startTime;

    private double endTime;

    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
