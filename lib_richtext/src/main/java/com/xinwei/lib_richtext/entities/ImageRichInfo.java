package com.xinwei.lib_richtext.entities;

import android.text.TextUtils;

/**
 * 图片
 * Created by xinwei2 on 2020/3/5
 */

public class ImageRichInfo extends BaseRichTextInfo {

    private String imageUrl;

    public ImageRichInfo() {
        setType(RichTextType.TYPE_IMAGE);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl) && !imageUrl.startsWith("file:///")) {
            imageUrl = "file:///" + imageUrl;
        }
        this.imageUrl = imageUrl;
    }
}
