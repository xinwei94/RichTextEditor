package com.xinwei.lib_richtext.entities;

/**
 * 发言人信息
 *
 * Created by xinwei2 on 2020/4/8
 */
public class SpeakerInfo {

    private String id;

    private String name;

    public SpeakerInfo() {
    }

    public SpeakerInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
