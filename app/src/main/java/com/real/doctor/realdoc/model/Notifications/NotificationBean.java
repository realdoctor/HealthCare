package com.real.doctor.realdoc.model.Notifications;

import java.util.Date;

public class NotificationBean {
    private String type;
    private String time;
    private String title;

    public NotificationBean(){}

    public NotificationBean(String type, String time, String title){
        this.type = type;
        this.time = time;
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
