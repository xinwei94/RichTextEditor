package com.xinwei.lib_richtext.entities;

/**
 * 发言人信息
 *
 * Created by xinwei2 on 2020/4/8
 */
public class SpeakerInfo {

    private int id;

    private String name;

    public SpeakerInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
