package com.xinwei.lib_richtext.entities;

/**
 * 支持的富文本类型
 * Created by xinwei2 on 2020/3/5
 */

public interface RichTextType {

    /**
     * 纯文本
     */
    int TYPE_TEXT = 0;

    /**
     * 图片
     */
    int TYPE_IMAGE = 1;

    /**
     * 图片加文字
     */
    int TYPE_IMAGE_TEXT = 2;
}
