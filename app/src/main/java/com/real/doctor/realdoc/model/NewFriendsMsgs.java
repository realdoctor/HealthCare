package com.real.doctor.realdoc.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class NewFriendsMsgs {
    @Id
    private String id;
    private String userName;
    private String groupId;
    private String groupName;
    private String time;
    private String reason;
    private String status;
    private String isInviteFromMe;
    private String groupInviter;
    private String unreadMsgCount;

    public NewFriendsMsgs() {
    }

    @Generated(hash = 1542864326)
    public NewFriendsMsgs(String id, String userName, String groupId,
            String groupName, String time, String reason, String status,
            String isInviteFromMe, String groupInviter, String unreadMsgCount) {
        this.id = id;
        this.userName = userName;
        this.groupId = groupId;
        this.groupName = groupName;
        this.time = time;
        this.reason = reason;
        this.status = status;
        this.isInviteFromMe = isInviteFromMe;
        this.groupInviter = groupInviter;
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsInviteFromMe() {
        return isInviteFromMe;
    }

    public void setIsInviteFromMe(String isInviteFromMe) {
        this.isInviteFromMe = isInviteFromMe;
    }

    public String getGroupInviter() {
        return groupInviter;
    }

    public void setGroupInviter(String groupInviter) {
        this.groupInviter = groupInviter;
    }

    public String getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(String unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }
}
