package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PatientBean extends UserBean implements Parcelable {

    private String addTime;
    private String src;
    private String remark;
    private String questionId;
    private String title;
    private String question;
    private String patientRecordId;
    private String status;
    private String messageId;
    private String doctorRealName;

    public PatientBean() {
    }

    protected PatientBean(Parcel in) {
        super(in);
        addTime = in.readString();
        src = in.readString();
        remark = in.readString();
        questionId = in.readString();
        title = in.readString();
        question = in.readString();
        patientRecordId = in.readString();
        status = in.readString();
        messageId = in.readString();
        doctorRealName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(addTime);
        dest.writeString(src);
        dest.writeString(remark);
        dest.writeString(questionId);
        dest.writeString(title);
        dest.writeString(question);
        dest.writeString(patientRecordId);
        dest.writeString(status);
        dest.writeString(messageId);
        dest.writeString(doctorRealName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PatientBean> CREATOR = new Creator<PatientBean>() {
        @Override
        public PatientBean createFromParcel(Parcel in) {
            return new PatientBean(in);
        }

        @Override
        public PatientBean[] newArray(int size) {
            return new PatientBean[size];
        }
    };

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getPatientRecordId() {
        return patientRecordId;
    }

    public void setPatientRecordId(String patientRecordId) {
        this.patientRecordId = patientRecordId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDoctorRealName() {
        return doctorRealName;
    }

    public void setDoctorRealName(String doctorRealName) {
        this.doctorRealName = doctorRealName;
    }
}
