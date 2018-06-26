package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MessageBean implements Parcelable {
    @Id
    private String id;
    private String username;
    private String groupid;
    private String groupname;

    private String time;
    private String reason;
    private String status;
    private String isInviteFromMe;
    private String groupinviter;

    private String unreadMsgCount;

    public MessageBean() {
    }

    protected MessageBean(Parcel in) {
        id = in.readString();
        username = in.readString();
        groupid = in.readString();
        groupname = in.readString();
        time = in.readString();
        reason = in.readString();
        status = in.readString();
        isInviteFromMe = in.readString();
        groupinviter = in.readString();
        unreadMsgCount = in.readString();
    }

    @Generated(hash = 2049764742)
    public MessageBean(String id, String username, String groupid, String groupname,
            String time, String reason, String status, String isInviteFromMe,
            String groupinviter, String unreadMsgCount) {
        this.id = id;
        this.username = username;
        this.groupid = groupid;
        this.groupname = groupname;
        this.time = time;
        this.reason = reason;
        this.status = status;
        this.isInviteFromMe = isInviteFromMe;
        this.groupinviter = groupinviter;
        this.unreadMsgCount = unreadMsgCount;
    }

    public static final Creator<MessageBean> CREATOR = new Creator<MessageBean>() {
        @Override
        public MessageBean createFromParcel(Parcel in) {
            return new MessageBean(in);
        }

        @Override
        public MessageBean[] newArray(int size) {
            return new MessageBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(groupid);
        dest.writeString(groupname);
        dest.writeString(time);
        dest.writeString(reason);
        dest.writeString(status);
        dest.writeString(isInviteFromMe);
        dest.writeString(groupinviter);
        dest.writeString(unreadMsgCount);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroupid() {
        return this.groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return this.groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsInviteFromMe() {
        return this.isInviteFromMe;
    }

    public void setIsInviteFromMe(String isInviteFromMe) {
        this.isInviteFromMe = isInviteFromMe;
    }

    public String getGroupinviter() {
        return this.groupinviter;
    }

    public void setGroupinviter(String groupinviter) {
        this.groupinviter = groupinviter;
    }

    public String getUnreadMsgCount() {
        return this.unreadMsgCount;
    }

    public void setUnreadMsgCount(String unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }
}
