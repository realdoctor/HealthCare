package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AddLabelBean implements Parcelable {
    private String name;
    private String icon;

    public AddLabelBean() {
    }

    protected AddLabelBean(Parcel in) {
        name = in.readString();
        icon = in.readString();
    }

    public static final Creator<AddLabelBean> CREATOR = new Creator<AddLabelBean>() {
        @Override
        public AddLabelBean createFromParcel(Parcel in) {
            return new AddLabelBean(in);
        }

        @Override
        public AddLabelBean[] newArray(int size) {
            return new AddLabelBean[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(icon);
    }
}
