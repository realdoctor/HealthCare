package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class DoctorBean extends UserBean implements Parcelable {

    private String doctorCode;
    //    private String doctorImg;
    private String visitOrgName;
    @SerializedName(value = "diagName", alternate = {"title"})
    private String diagName;
    private String visitDtime;
    private String questionId;
    private String patientRecordId;
    @SerializedName("question")
    private String inquery;
    private String answer;
    private String addTime;
    private String retryNum;
    private String doctorRealName;
    private String hospitalId;
    private String deptName;
    private String deptCode;
    private String messageId;

    protected DoctorBean(Parcel in) {
        super(in);
        doctorCode = in.readString();
        visitOrgName = in.readString();
        diagName = in.readString();
        visitDtime = in.readString();
        questionId = in.readString();
        patientRecordId = in.readString();
        inquery = in.readString();
        answer = in.readString();
        addTime = in.readString();
        retryNum = in.readString();
        doctorRealName = in.readString();
        hospitalId = in.readString();
        deptName = in.readString();
        deptCode = in.readString();
        messageId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(doctorCode);
        dest.writeString(visitOrgName);
        dest.writeString(diagName);
        dest.writeString(visitDtime);
        dest.writeString(questionId);
        dest.writeString(patientRecordId);
        dest.writeString(inquery);
        dest.writeString(answer);
        dest.writeString(addTime);
        dest.writeString(retryNum);
        dest.writeString(doctorRealName);
        dest.writeString(hospitalId);
        dest.writeString(deptName);
        dest.writeString(deptCode);
        dest.writeString(messageId);
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

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public String getInquery() {
        return inquery;
    }

    public void setInquery(String inquery) {
        this.inquery = inquery;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(String retryNum) {
        this.retryNum = retryNum;
    }

    public String getDoctorRealName() {
        return doctorRealName;
    }

    public void setDoctorRealName(String doctorRealName) {
        this.doctorRealName = doctorRealName;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
