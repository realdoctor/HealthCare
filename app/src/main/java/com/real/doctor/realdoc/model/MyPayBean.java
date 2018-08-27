package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyPayBean implements Parcelable {

    private String orderNo;
    private String addTime;
    private String lastUpdateDtime;
    private String remark;
    private String message;
    private String type;
    private String userName;
    private String userId;
    private String toUser;
    private String money;
    private String toUserPicUrl;
    private String userPicUrl;
    private String payFrom;
    private String toUserName;
    private String id;
    private String mark;
    private String status;

    public MyPayBean() {
    }

    protected MyPayBean(Parcel in) {
        orderNo = in.readString();
        addTime = in.readString();
        lastUpdateDtime = in.readString();
        remark = in.readString();
        message = in.readString();
        type = in.readString();
        userName = in.readString();
        userId = in.readString();
        toUser = in.readString();
        money = in.readString();
        toUserPicUrl = in.readString();
        userPicUrl = in.readString();
        payFrom = in.readString();
        toUserName = in.readString();
        id = in.readString();
        mark = in.readString();
        status = in.readString();
    }

    public static final Creator<MyPayBean> CREATOR = new Creator<MyPayBean>() {
        @Override
        public MyPayBean createFromParcel(Parcel in) {
            return new MyPayBean(in);
        }

        @Override
        public MyPayBean[] newArray(int size) {
            return new MyPayBean[size];
        }
    };

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getLastUpdateDtime() {
        return lastUpdateDtime;
    }

    public void setLastUpdateDtime(String lastUpdateDtime) {
        this.lastUpdateDtime = lastUpdateDtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getToUserPicUrl() {
        return toUserPicUrl;
    }

    public void setToUserPicUrl(String toUserPicUrl) {
        this.toUserPicUrl = toUserPicUrl;
    }

    public String getUserPicUrl() {
        return userPicUrl;
    }

    public void setUserPicUrl(String userPicUrl) {
        this.userPicUrl = userPicUrl;
    }

    public String getPayFrom() {
        return payFrom;
    }

    public void setPayFrom(String payFrom) {
        this.payFrom = payFrom;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderNo);
        dest.writeString(addTime);
        dest.writeString(lastUpdateDtime);
        dest.writeString(remark);
        dest.writeString(message);
        dest.writeString(type);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(toUser);
        dest.writeString(money);
        dest.writeString(toUserPicUrl);
        dest.writeString(userPicUrl);
        dest.writeString(payFrom);
        dest.writeString(toUserName);
        dest.writeString(id);
        dest.writeString(mark);
        dest.writeString(status);
    }
}
