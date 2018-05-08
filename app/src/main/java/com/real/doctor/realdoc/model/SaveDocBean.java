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
    @SerializedName("diagCode")
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
    @SerializedName("visitDtime")
    private String time;
    //存放病历图片的文件夹
    private String folder;
    //病历图片的名称
    private String imgs;
    //医嘱(每张病历图片一个医嘱)
    private String advice;
    //机构编码
    private String orgCode;
    //诊断信息id
    private String patientDiagId;
    //病人ID
    private String patientId;
    // 就诊科室名称
    private String visitDeptName;
    //就诊通道(1、门诊，2、住院)
    private String visitWay;
    //是否选中病历
    private boolean isSelect = false;

    public SaveDocBean() {
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
        orgCode = in.readString();
        patientDiagId = in.readString();
        patientId = in.readString();
        visitDeptName = in.readString();
        visitWay = in.readString();
        isSelect = in.readByte() != 0;
    }


    @Generated(hash = 434754401)
    public SaveDocBean(String id, String ill, String hospital, String doctor,
            String time, String folder, String imgs, String advice, String orgCode,
            String patientDiagId, String patientId, String visitDeptName,
            String visitWay, boolean isSelect) {
        this.id = id;
        this.ill = ill;
        this.hospital = hospital;
        this.doctor = doctor;
        this.time = time;
        this.folder = folder;
        this.imgs = imgs;
        this.advice = advice;
        this.orgCode = orgCode;
        this.patientDiagId = patientDiagId;
        this.patientId = patientId;
        this.visitDeptName = visitDeptName;
        this.visitWay = visitWay;
        this.isSelect = isSelect;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(ill);
        parcel.writeString(hospital);
        parcel.writeString(doctor);
        parcel.writeString(time);
        parcel.writeString(folder);
        parcel.writeString(imgs);
        parcel.writeString(advice);
        parcel.writeString(orgCode);
        parcel.writeString(patientDiagId);
        parcel.writeString(patientId);
        parcel.writeString(visitDeptName);
        parcel.writeString(visitWay);
        parcel.writeByte((byte) (isSelect ? 1 : 0));
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


    public String getTime() {
        return this.time;
    }


    public void setTime(String time) {
        this.time = time;
    }


    public String getFolder() {
        return this.folder;
    }


    public void setFolder(String folder) {
        this.folder = folder;
    }


    public String getImgs() {
        return this.imgs;
    }


    public void setImgs(String imgs) {
        this.imgs = imgs;
    }


    public String getAdvice() {
        return this.advice;
    }


    public void setAdvice(String advice) {
        this.advice = advice;
    }


    public String getOrgCode() {
        return this.orgCode;
    }


    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }


    public String getPatientDiagId() {
        return this.patientDiagId;
    }


    public void setPatientDiagId(String patientDiagId) {
        this.patientDiagId = patientDiagId;
    }


    public String getPatientId() {
        return this.patientId;
    }


    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public String getVisitDeptName() {
        return this.visitDeptName;
    }


    public void setVisitDeptName(String visitDeptName) {
        this.visitDeptName = visitDeptName;
    }


    public String getVisitWay() {
        return this.visitWay;
    }


    public void setVisitWay(String visitWay) {
        this.visitWay = visitWay;
    }


    public boolean getIsSelect() {
        return this.isSelect;
    }


    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
