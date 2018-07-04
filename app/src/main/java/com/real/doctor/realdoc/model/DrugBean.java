package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class DrugBean implements Parcelable {

    @Id
    private String drugCode;
    private String recordId;
    private String drugName;//药物名称
    private String drugStdCode;//	药物标准编码
    private String drugStdName;//	药物标准名称

    public DrugBean() {
    }
    
    protected DrugBean(Parcel in) {
        drugCode = in.readString();
        recordId = in.readString();
        drugName = in.readString();
        drugStdCode = in.readString();
        drugStdName = in.readString();
    }

    @Generated(hash = 276299622)
    public DrugBean(String drugCode, String recordId, String drugName,
            String drugStdCode, String drugStdName) {
        this.drugCode = drugCode;
        this.recordId = recordId;
        this.drugName = drugName;
        this.drugStdCode = drugStdCode;
        this.drugStdName = drugStdName;
    }

    public static final Creator<DrugBean> CREATOR = new Creator<DrugBean>() {
        @Override
        public DrugBean createFromParcel(Parcel in) {
            return new DrugBean(in);
        }

        @Override
        public DrugBean[] newArray(int size) {
            return new DrugBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(drugCode);
        dest.writeString(recordId);
        dest.writeString(drugName);
        dest.writeString(drugStdCode);
        dest.writeString(drugStdName);
    }

    public String getDrugCode() {
        return this.drugCode;
    }

    public void setDrugCode(String drugCode) {
        this.drugCode = drugCode;
    }

    public String getRecordId() {
        return this.recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDrugName() {
        return this.drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugStdCode() {
        return this.drugStdCode;
    }

    public void setDrugStdCode(String drugStdCode) {
        this.drugStdCode = drugStdCode;
    }

    public String getDrugStdName() {
        return this.drugStdName;
    }

    public void setDrugStdName(String drugStdName) {
        this.drugStdName = drugStdName;
    }
}
