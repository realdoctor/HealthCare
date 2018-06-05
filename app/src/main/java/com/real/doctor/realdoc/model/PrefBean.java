package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PrefBean implements Parcelable {

    @Id
    private String disabled_groups;
    private String disabled_ids;

    protected PrefBean(Parcel in) {
        disabled_groups = in.readString();
        disabled_ids = in.readString();
    }

    @Generated(hash = 1789943793)
    public PrefBean(String disabled_groups, String disabled_ids) {
        this.disabled_groups = disabled_groups;
        this.disabled_ids = disabled_ids;
    }

    @Generated(hash = 643294906)
    public PrefBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(disabled_groups);
        dest.writeString(disabled_ids);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDisabled_groups() {
        return this.disabled_groups;
    }

    public void setDisabled_groups(String disabled_groups) {
        this.disabled_groups = disabled_groups;
    }

    public String getDisabled_ids() {
        return this.disabled_ids;
    }

    public void setDisabled_ids(String disabled_ids) {
        this.disabled_ids = disabled_ids;
    }

    public static final Creator<PrefBean> CREATOR = new Creator<PrefBean>() {
        @Override
        public PrefBean createFromParcel(Parcel in) {
            return new PrefBean(in);
        }

        @Override
        public PrefBean[] newArray(int size) {
            return new PrefBean[size];
        }
    };
}
