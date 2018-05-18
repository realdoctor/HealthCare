package com.real.doctor.realdoc.API.NotificationsPOJO;


import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationBody implements Serializable
{

    @SerializedName("addTime")
    @Expose
    private Long addTime;
    @SerializedName("dataList")
    @Expose
    private List<NotificationMessage> dataList = null;
    @SerializedName("noticeTypeId")
    @Expose
    private String noticeTypeId;
    @SerializedName("noticeType")
    @Expose
    private String noticeType;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("noticeMessageId")
    @Expose
    private Integer noticeMessageId;
    private final static long serialVersionUID = 6566167207267014258L;

    public Long getAddTime() {
        return addTime;
    }

    public void setAddTime(Long addTime) {
        this.addTime = addTime;
    }

    public List<NotificationMessage> getDataList() {
        return dataList;
    }

    public void setDataList(List<NotificationMessage> dataList) {
        this.dataList = dataList;
    }

    public String getNoticeTypeId() {
        return noticeTypeId;
    }

    public void setNoticeTypeId(String noticeTypeId) {
        this.noticeTypeId = noticeTypeId;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getNoticeMessageId() {
        return noticeMessageId;
    }

    public void setNoticeMessageId(Integer noticeMessageId) {
        this.noticeMessageId = noticeMessageId;
    }

}
