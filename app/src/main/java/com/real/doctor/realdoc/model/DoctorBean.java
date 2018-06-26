package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DoctorBean extends UserBean implements Parcelable {

    private String doctorCode;
    //    private String doctorImg;
    //这个字段本身来说是多余的,但后台给的数据要和这边匹配,才能解析
    private String respDoctorName;
    private String visitOrgName;
    private String diagName;
    private String visitDtime;

    public DoctorBean() {
    }

    protected DoctorBean(Parcel in) {
        doctorCode = in.readString();
        respDoctorName = in.readString();
        visitOrgName = in.readString();
        diagName = in.readString();
        visitDtime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doctorCode);
        dest.writeString(respDoctorName);
        dest.writeString(visitOrgName);
        dest.writeString(diagName);
        dest.writeString(visitDtime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DoctorBean> CREATOR = new Creator<DoctorBean>() {
        @Override
        public DoctorBean createFromParcel(Parcel in) {
            return new DoctorBean(in);
        }

        @Override
        public DoctorBean[] newArray(int size) {
            return new DoctorBean[size];
        }
    };

    public String getDoctorCode() {
        return doctorCode;
    }

    public void setDoctorCode(String doctorCode) {
        this.doctorCode = doctorCode;
    }

    public String getRespDoctorName() {
        return respDoctorName;
    }

    public void setRespDoctorName(String respDoctorName) {
        this.respDoctorName = respDoctorName;
    }

    public String getVisitOrgName() {
        return visitOrgName;
    }

    public void setVisitOrgName(String visitOrgName) {
        this.visitOrgName = visitOrgName;
    }

    public String getDiagName() {
        return diagName;
    }

    public void setDiagName(String diagName) {
        this.diagName = diagName;
    }

    public String getVisitDtime() {
        return visitDtime;
    }

    public void setVisitDtime(String visitDtime) {
        this.visitDtime = visitDtime;
    }
}
