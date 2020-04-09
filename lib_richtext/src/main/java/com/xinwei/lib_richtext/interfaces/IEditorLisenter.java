package com.xinwei.lib_richtext.interfaces;

import com.xinwei.lib_richtext.entities.BaseRichTextInfo;
import com.xinwei.lib_richtext.entities.ImageRichInfo;
import com.xinwei.lib_richtext.entities.ImageTextRichInfo;
import com.xinwei.lib_richtext.entities.MoreBtnItemInfo;
import com.xinwei.lib_richtext.entities.RichTextData;
import com.xinwei.lib_richtext.entities.SpeakerInfo;
import com.xinwei.lib_richtext.entities.TextRichInfo;


/**
 * 富文本编辑器对外回调
 * Created by xinwei2 on 2020/3/6
 */

public interface IEditorLisenter {

    /**
     * 初始化完成
     */
    void onInitFinish();

    /**
     * 点击文本
     *
     * @param info 文本信息
     */
    void onClickText(TextRichInfo info);

    /**
     * 点击图片
     *
     * @param info 图片信息
     */
    void onClickImage(ImageRichInfo info);

    /**
     * 点击图文框
     *
     * @param info 图文信息
     */
    void onClickImageText(ImageTextRichInfo info);

    /**
     * 点击更多条目按钮
     *
     * @param itemInfo 更多按钮条目信息
     * @param richTextInfo 宿主富文本信息
     */
    void onClickMoreBtnItem(MoreBtnItemInfo itemInfo, BaseRichTextInfo richTextInfo);

    /**
     * 点击发言人
     *
     * @param speakerInfo 发言人信息
     */
    void onClickSpeaker(SpeakerInfo speakerInfo);

    /**
     * 文档内容更新
     *
     * @param richTextData 更新后的文档信息
     */
    void updateContent(RichTextData richTextData);
}
