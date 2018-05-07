package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/4/24.
 */
@Entity
public class SaveDocBean implements Parcelable {

    @Id
    private String id;
    //疾病
    @SerializedName("diagName")
    private String ill;
    //医院
    @SerializedName("visitOrgName")
    private String hospital;
    //医生
    @SerializedName("respDoctorName")
    private String doctor;
    //就诊时间
    @SerializedName("lastUpdateDtime")
    private String time;
    //存放病历图片的文件夹
    private String folder;
    //病历图片的名称
    private String imgs;
    //医嘱(每张病历图片一个医嘱)
    private String advice;
    //是否选中病历
    private boolean isSelect = false;

    public SaveDocBean() {
    }

    @Generated(hash = 1448899373)
    public SaveDocBean(String id, String ill, String hospital, String doctor,
                       String time, String folder, String imgs, String advice,
                       boolean isSelect) {
        this.id = id;
        this.ill = ill;
        this.hospital = hospital;
        this.doctor = doctor;
        this.time = time;
        this.folder = folder;
        this.imgs = imgs;
        this.advice = advice;
        this.isSelect = isSelect;
    }

    protected SaveDocBean(Parcel in) {
        id = in.readString();
        ill = in.readString();
        hospital = in.readString();
        doctor = in.readString();
        time = in.readString();
        folder = in.readString();
        imgs = in.readString();
        advice = in.readString();
        isSelect = in.readByte() != 0;
    }

    public static final Creator<SaveDocBean> CREATOR = new Creator<SaveDocBean>() {
        @Override
        public SaveDocBean createFromParcel(Parcel in) {
            return new SaveDocBean(in);
        }

        @Override
        public SaveDocBean[] newArray(int size) {
            return new SaveDocBean[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(ill);
        dest.writeString(hospital);
        dest.writeString(doctor);
        dest.writeString(time);
        dest.writeString(folder);
        dest.writeString(imgs);
        dest.writeString(advice);
        dest.writeByte((byte) (isSelect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getId() {
        return this.id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getIll() {
        return this.ill;
    }


    public void setIll(String ill) {
        this.ill = ill;
    }


    public String getHospital() {
        return this.hospital;
    }


    public void setHospital(String hospital) {
        this.hospital = hospital;
    }


    public String getDoctor() {
        return this.doctor;
    }


    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }


    public String getImgs() {
        return this.imgs;
    }


    public void setImgs(String imgs) {
        this.imgs = imgs;
    }


    public boolean getIsSelect() {
        return this.isSelect;
    }


    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }


    public String getFolder() {
        return this.folder;
    }


    public void setFolder(String folder) {
        this.folder = folder;
    }


    public String getTime() {
        return this.time;
    }


    public void setTime(String time) {
        this.time = time;
    }


    public String getAdvice() {
        return this.advice;
    }


    public void setAdvice(String advice) {
        this.advice = advice;
    }

}
