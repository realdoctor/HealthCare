package com.real.doctor.realdoc.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class RecordTableBean implements MultiItemEntity {
    public static final int HEADER_TYPE = 1;
    public static final int CONTENT_TYPE = 2;
    private int itemType;

    private String content;
    private String fristContent;
    private String secondContent;

    public RecordTableBean() {
    }

    public RecordTableBean(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getFristContent() {
        return fristContent;
    }

    public void setFristContent(String fristContent) {
        this.fristContent = fristContent;
    }

    public String getSecondContent() {
        return secondContent;
    }

    public void setSecondContent(String secondContent) {
        this.secondContent = secondContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}