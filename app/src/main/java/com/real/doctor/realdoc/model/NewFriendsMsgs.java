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

    public enum InviteMessageStatus {

        //==contact
        /**
         * being invited
         */
        BEINVITEED,
        /**
         * being refused
         */
        BEREFUSED,
        /**
         * remote user already agreed
         */
        BEAGREED,

        //==group application
        /**
         * remote user apply to join
         */
        BEAPPLYED,
        /**
         * you have agreed to join
         */
        AGREED,
        /**
         * you refused the join request
         */
        REFUSED,

        //==group invitation
        /**
         * received remote user's invitation
         **/
        GROUPINVITATION,
        /**
         * remote user accept your invitation
         **/
        GROUPINVITATION_ACCEPTED,
        /**
         * remote user declined your invitation
         **/
        GROUPINVITATION_DECLINED,

        //==multi-device
        /**
         * current user accept contact invitation in other device
         **/
        MULTI_DEVICE_CONTACT_ACCEPT,
        /**
         * current user decline contact invitation in other device
         **/
        MULTI_DEVICE_CONTACT_DECLINE,
        /**
         * current user send contact invite in other device
         **/
        MULTI_DEVICE_CONTACT_ADD,
        /**
         * current user add black list in other device
         **/
        MULTI_DEVICE_CONTACT_BAN,
        /**
         * current user remove someone from black list in other device
         **/
        MULTI_DEVICE_CONTACT_ALLOW,

        /**
         * current user create group in other device
         */
        MULTI_DEVICE_GROUP_CREATE,
        /**
         * current user destroy group in other device
         */
        MULTI_DEVICE_GROUP_DESTROY,
        /**
         * current user join group in other device
         */
        MULTI_DEVICE_GROUP_JOIN,
        /**
         * current user leave group in other device
         */
        MULTI_DEVICE_GROUP_LEAVE,
        /**
         * current user apply to join group in other device
         */
        MULTI_DEVICE_GROUP_APPLY,
        /**
         * current user accept group application in other device
         */
        MULTI_DEVICE_GROUP_APPLY_ACCEPT,
        /**
         * current user refuse group application in other device
         */
        MULTI_DEVICE_GROUP_APPLY_DECLINE,
        /**
         * current user invite some join group in other device
         */
        MULTI_DEVICE_GROUP_INVITE,
        /**
         * current user accept group invitation in other device
         */
        MULTI_DEVICE_GROUP_INVITE_ACCEPT,
        /**
         * current user decline group invitation in other device
         */
        MULTI_DEVICE_GROUP_INVITE_DECLINE,
        /**
         * current user kick some one out of group in other device
         */
        MULTI_DEVICE_GROUP_KICK,
        /**
         * current user add some one into group black list in other device
         */
        MULTI_DEVICE_GROUP_BAN,
        /**
         * current user remove some one from group black list in other device
         */
        MULTI_DEVICE_GROUP_ALLOW,
        /**
         * current user block group message in other device
         */
        MULTI_DEVICE_GROUP_BLOCK,
        /**
         * current user unblock group message in other device
         */
        MULTI_DEVICE_GROUP_UNBLOCK,
        /**
         * current user assign group owner to some one else in other device
         */
        MULTI_DEVICE_GROUP_ASSIGN_OWNER,
        /**
         * current user add group admin in other device
         */
        MULTI_DEVICE_GROUP_ADD_ADMIN,
        /**
         * current user remove group admin in other device
         */
        MULTI_DEVICE_GROUP_REMOVE_ADMIN,
        /**
         * current user mute some one in group in other device
         */
        MULTI_DEVICE_GROUP_ADD_MUTE,
        /**
         * current user unmute some one in group in other device
         */
        MULTI_DEVICE_GROUP_REMOVE_MUTE
    }

}
