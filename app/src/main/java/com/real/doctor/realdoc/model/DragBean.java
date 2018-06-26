package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DragBean implements Parcelable {
    private String id;
    private String drugName;//药物名称
    private String drugStdCode;//	药物标准编码
    private String drugStdName;//	药物标准名称

    public DragBean() {
    }

    protected DragBean(Parcel in) {
        id = in.readString();
        drugName = in.readString();
        drugStdCode = in.readString();
        drugStdName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(drugName);
        dest.writeString(drugStdCode);
        dest.writeString(drugStdName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DragBean> CREATOR = new Creator<DragBean>() {
        @Override
        public DragBean createFromParcel(Parcel in) {
            return new DragBean(in);
        }

        @Override
        public DragBean[] newArray(int size) {
            return new DragBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugStdCode() {
        return drugStdCode;
    }

    public void setDrugStdCode(String drugStdCode) {
        this.drugStdCode = drugStdCode;
    }

    public String getDrugStdName() {
        return drugStdName;
    }

    public void setDrugStdName(String drugStdName) {
        this.drugStdName = drugStdName;
    }
}
