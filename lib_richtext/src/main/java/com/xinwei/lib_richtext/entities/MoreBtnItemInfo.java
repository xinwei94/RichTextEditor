package com.xinwei.lib_richtext.entities;

/**
 * 自定义更多按钮条目信息
 * Created by xinwei2 on 2020/3/12
 */

public class MoreBtnItemInfo {

    private int type;

    private String text;

    public MoreBtnItemInfo() {
    }

    public MoreBtnItemInfo(int type, String text) {
        this.type = type;
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
