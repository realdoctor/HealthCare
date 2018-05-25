package com.real.doctor.realdoc.API.NotificationsPOJO;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationMessage implements Serializable
{

    @SerializedName("noticeDiagDrugId")
    @Expose
    private Integer noticeDiagDrugId;
    @SerializedName("noticeMessageId")
    @Expose
    private String noticeMessageId;
    @SerializedName("stdCode")
    @Expose
    private String stdCode;
    @SerializedName("stdName")
    @Expose
    private String stdName;
    @SerializedName("noticeCommentId")
    @Expose
    private Integer noticeCommentId;
    private final static long serialVersionUID = -1323275709652909294L;

    public Integer getNoticeDiagDrugId() {
        return noticeDiagDrugId;
    }

    public void setNoticeDiagDrugId(Integer noticeDiagDrugId) {
        this.noticeDiagDrugId = noticeDiagDrugId;
    }

    public String getNoticeMessageId() {
        return noticeMessageId;
    }

    public void setNoticeMessageId(String noticeMessageId) {
        this.noticeMessageId = noticeMessageId;
    }

    public String getStdCode() {
        return stdCode;
    }

    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public Integer getNoticeCommentId() {
        return noticeCommentId;
    }

    public void setNoticeCommentId(Integer noticeCommentId) {
        this.noticeCommentId = noticeCommentId;
    }

}