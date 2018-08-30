package com.real.doctor.realdoc.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@SuppressLint("ParcelCreator")
@Entity
public class PushInfoBean implements Parcelable {
    //用户名唯一
    @Id(autoincrement = true)
    private Long id;
    private String time;
    private String userId;
    private String title;
    private String content;
    private String tabId;
    //聊天字段(本来想通过继承分成三个类的,但我觉得完全没必要,一张表就行)
    private String fromMobile;
    private String fromUserId;

    @Generated(hash = 1860605186)
    public PushInfoBean(Long id, String time, String userId, String title,
                        String content, String tabId, String fromMobile, String fromUserId) {
        this.id = id;
        this.time = time;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.tabId = tabId;
        this.fromMobile = fromMobile;
        this.fromUserId = fromUserId;
    }

    @Generated(hash = 1633278342)
    public PushInfoBean() {
    }

    protected PushInfoBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        time = in.readString();
        userId = in.readString();
        title = in.readString();
        content = in.readString();
        tabId = in.readString();
        fromMobile = in.readString();
        fromUserId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(time);
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(tabId);
        dest.writeString(fromMobile);
        dest.writeString(fromUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PushInfoBean> CREATOR = new Creator<PushInfoBean>() {
        @Override
        public PushInfoBean createFromParcel(Parcel in) {
            return new PushInfoBean(in);
        }

        @Override
        public PushInfoBean[] newArray(int size) {
            return new PushInfoBean[size];
        }
    };

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTabId() {
        return this.tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public String getFromMobile() {
        return this.fromMobile;
    }

    public void setFromMobile(String fromMobile) {
        this.fromMobile = fromMobile;
    }

    public String getFromUserId() {
        return this.fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

}
