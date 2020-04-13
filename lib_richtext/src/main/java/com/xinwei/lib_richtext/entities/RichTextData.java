package com.xinwei.lib_richtext.entities;

import java.util.List;

/**
 * 富文本内容数据
 * Created by xinwei2 on 2020/3/5
 */

public class RichTextData {

    List<BaseRichTextInfo> datalist;

    public RichTextData() {
    }

    public RichTextData(List<BaseRichTextInfo> dataList) {
        this.datalist = dataList;
    }

    public List<BaseRichTextInfo> getDataList() {
        return datalist;
    }

    public void setDataList(List<BaseRichTextInfo> dataList) {
        this.datalist = dataList;
    }
}
