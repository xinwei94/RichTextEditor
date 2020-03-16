package com.xinwei.lib_richtext.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.xinwei.lib_richtext.entities.BaseRichTextInfo;
import com.xinwei.lib_richtext.entities.ImageRichInfo;
import com.xinwei.lib_richtext.entities.ImageTextRichInfo;
import com.xinwei.lib_richtext.entities.MoreBtnItemInfo;
import com.xinwei.lib_richtext.entities.RichTextData;
import com.xinwei.lib_richtext.entities.RichTextType;
import com.xinwei.lib_richtext.entities.TextRichInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;

/**
 * Created by xinwei2 on 2020/3/7
 */

public class EditorUtils {

    private static final String TAG = "EditorUtils";

    public static RichTextData parseRichTextData(String json) {
        //Gson转换同父类不同子类列表处理
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseRichTextInfo.class, new JsonDeserializer<BaseRichTextInfo>() {
            @Override
            public BaseRichTextInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                int typeObj = jsonElement.getAsJsonObject().get("type").getAsInt();
                Type typeReal = TextRichInfo.class;
                if (RichTextType.TYPE_IMAGE == typeObj) {
                    typeReal = ImageRichInfo.class;
                } else if (RichTextType.TYPE_IMAGE_TEXT == typeObj) {
                    typeReal = ImageTextRichInfo.class;
                }
                return jsonDeserializationContext.deserialize(jsonElement, typeReal);
            }
        }).create();

        return gson.fromJson(json, RichTextData.class);
    }

    public static BaseRichTextInfo parseBaseRichTextInfo(String json) {
        //Gson转换同父类不同子类列表处理
        Gson gson = new GsonBuilder().registerTypeAdapter(BaseRichTextInfo.class, new JsonDeserializer<BaseRichTextInfo>() {
            @Override
            public BaseRichTextInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                int typeObj = jsonElement.getAsJsonObject().get("type").getAsInt();
                Type typeReal = TextRichInfo.class;
                if (RichTextType.TYPE_IMAGE == typeObj) {
                    typeReal = ImageRichInfo.class;
                } else if (RichTextType.TYPE_IMAGE_TEXT == typeObj) {
                    typeReal = ImageTextRichInfo.class;
                }
                return jsonDeserializationContext.deserialize(jsonElement, typeReal);
            }
        }).create();

        return gson.fromJson(json, BaseRichTextInfo.class);
    }

    public static TextRichInfo parseTextRichInfo(String json) {
        return new Gson().fromJson(json, TextRichInfo.class);
    }

    public static ImageRichInfo parseImageRichInfo(String json) {
        return new Gson().fromJson(json, ImageRichInfo.class);
    }

    public static ImageTextRichInfo parseImageTextRichInfo(String json) {
        return new Gson().fromJson(json, ImageTextRichInfo.class);
    }

    public static MoreBtnItemInfo parseMoreBtnItemInfo(String json) {
        return new Gson().fromJson(json, MoreBtnItemInfo.class);
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 保存文本到文件
     *
     * @param fileName
     * @param text
     * @param isWipe   是否擦除旧内容
     * @return
     */
    public static int writeString(String fileName, String text, boolean isWipe) {
        if (TextUtils.isEmpty(fileName)) {
            return 0;
        }
        int file_len = 0;
        RandomAccessFile tmp_file = null;
        try {
            File file = new File(fileName);
            // 增加目录判断
            if (file.getParent() == null) {
                return file_len;
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            tmp_file = new RandomAccessFile(file, "rw");
            if (isWipe) {
                tmp_file.setLength(0);
            }
            tmp_file.seek(tmp_file.length());
            tmp_file.write(text.getBytes("utf-8"));
            file_len = (int) tmp_file.length();
        } catch (IOException e) {
            Log.d(TAG, "", e);
            return file_len;
        }

        try {
            if (null != tmp_file) {
                tmp_file.close();
            }
        } catch (IOException e) {
            Log.d(TAG, "", e);
        }
        return file_len;
    }
}
