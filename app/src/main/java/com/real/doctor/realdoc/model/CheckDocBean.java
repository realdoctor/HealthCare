package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/4/25.
 */

public class CheckDocBean implements Parcelable {

    private String id;
    private String ill;
    private String hospital;
    private String doctor;
    private String imgs;
    private boolean isSelect = false;

    public CheckDocBean() {
    }

    protected CheckDocBean(Parcel in) {
        id = in.readString();
        ill = in.readString();
        hospital = in.readString();
        doctor = in.readString();
        imgs = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<CheckDocBean> CREATOR = new Creator<CheckDocBean>() {
        @Override
        public CheckDocBean createFromParcel(Parcel in) {
            return new CheckDocBean(in);
        }

        @Override
        public CheckDocBean[] newArray(int size) {
            return new CheckDocBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getIll() {
        return ill;

    }

    public void setIll(String ill) {
        this.ill = ill;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(ill);
        parcel.writeString(hospital);
        parcel.writeString(doctor);
        parcel.writeString(imgs);
        parcel.writeByte((byte) (isSelect ? 1 : 0));
    }
}
