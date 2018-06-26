package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class RobotBean implements Parcelable {

    @Id
    private String username;
    private String robots;
    private String nick;
    private String avatar;
    @Generated(hash = 905366901)
    public RobotBean(String username, String robots, String nick, String avatar) {
        this.username = username;
        this.robots = robots;
        this.nick = nick;
        this.avatar = avatar;
    }
    @Generated(hash = 940933227)
    public RobotBean() {
    }

    protected RobotBean(Parcel in) {
        username = in.readString();
        robots = in.readString();
        nick = in.readString();
        avatar = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(robots);
        dest.writeString(nick);
        dest.writeString(avatar);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RobotBean> CREATOR = new Creator<RobotBean>() {
        @Override
        public RobotBean createFromParcel(Parcel in) {
            return new RobotBean(in);
        }

        @Override
        public RobotBean[] newArray(int size) {
            return new RobotBean[size];
        }
    };

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRobots() {
        return this.robots;
    }
    public void setRobots(String robots) {
        this.robots = robots;
    }
    public String getNick() {
        return this.nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
