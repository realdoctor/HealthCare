package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hyphenate.chat.EMContact;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/4/19.
 */
@Entity
public class UserBean extends EMContact implements Parcelable {
    @Id
    @SerializedName(value = "userId", alternate = {"doctorUserId"})
    private String id;
    private String roleId;
    @SerializedName("originalImageUrl")
    private String avater;
    @SerializedName(value = "userName", alternate = {"doctorRealName", "patientRealName", "respDoctorName"})
    private String name;
    private String pwd;
    @SerializedName("mobilePhone")
    private String mobile;
    @SerializedName("realName")
    private String realname;
    @SerializedName("lastUpdateDtime")
    private String lastTime;
    @SerializedName("sentAddress")
    private String address;
    private String email;

    public UserBean() {
    }

    @Generated(hash = 885127356)
    public UserBean(String id, String roleId, String avater, String name, String pwd,
                    String mobile, String realname, String lastTime, String address, String email) {
        this.id = id;
        this.roleId = roleId;
        this.avater = avater;
        this.name = name;
        this.pwd = pwd;
        this.mobile = mobile;
        this.realname = realname;
        this.lastTime = lastTime;
        this.address = address;
        this.email = email;
    }

    protected UserBean(Parcel in) {
        super();
        id = in.readString();
        roleId = in.readString();
        avater = in.readString();
        name = in.readString();
        pwd = in.readString();
        mobile = in.readString();
        realname = in.readString();
        lastTime = in.readString();
        address = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(id);
        dest.writeString(roleId);
        dest.writeString(avater);
        dest.writeString(name);
        dest.writeString(pwd);
        dest.writeString(mobile);
        dest.writeString(realname);
        dest.writeString(lastTime);
        dest.writeString(address);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel in) {
            return new UserBean(in);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getAvater() {
        return this.avater;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
