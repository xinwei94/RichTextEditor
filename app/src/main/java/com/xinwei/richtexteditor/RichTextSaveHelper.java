package com.xinwei.richtexteditor;

import android.os.Environment;

import com.xinwei.lib_richtext.entities.RichTextData;

import java.io.File;

/**
 * Created by Zn on 2020/3/6
 */
public class RichTextSaveHelper {

    public static final String DOC_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "EditorWord.doc";

    private static RichTextData mRichTextData;

    public static RichTextData getRichTextData() {
        return mRichTextData;
    }

    public static void setRichTextData(RichTextData richTextData) {
        mRichTextData = richTextData;
    }
}
