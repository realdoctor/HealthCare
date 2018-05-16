package com.real.doctor.realdoc.model.Notifications;

public class NotificationRecordBean extends NotificationBean {

    private String notice;
    private String code;

    public NotificationRecordBean(){

    }

    public NotificationRecordBean(String notice, String code){
        this.notice = notice;
        this.code = code;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
