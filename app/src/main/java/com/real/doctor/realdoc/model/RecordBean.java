package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecordBean implements Parcelable {
    @Id
    private String fileName;
    private String filePath;
    private String elapsedMillis;
    private String date;
    private String folder;
    private String advice;
    private String recordId;
    //如果该字段是空,则该病历是本地的病历，否则如果是"1",就是患者传给医生的病历
    private String isPatient;

    @Generated(hash = 686652382)
    public RecordBean(String fileName, String filePath, String elapsedMillis,
                      String date, String folder, String advice, String recordId,
                      String isPatient) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.elapsedMillis = elapsedMillis;
        this.date = date;
        this.folder = folder;
        this.advice = advice;
        this.recordId = recordId;
        this.isPatient = isPatient;
    }

    @Generated(hash = 96196931)
    public RecordBean() {
    }

    protected RecordBean(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        elapsedMillis = in.readString();
        date = in.readString();
        folder = in.readString();
        advice = in.readString();
        recordId = in.readString();
        isPatient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeString(elapsedMillis);
        dest.writeString(date);
        dest.writeString(folder);
        dest.writeString(advice);
        dest.writeString(recordId);
        dest.writeString(isPatient);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecordBean> CREATOR = new Creator<RecordBean>() {
        @Override
        public RecordBean createFromParcel(Parcel in) {
            return new RecordBean(in);
        }

        @Override
        public RecordBean[] newArray(int size) {
            return new RecordBean[size];
        }
    };

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getElapsedMillis() {
        return this.elapsedMillis;
    }

    public void setElapsedMillis(String elapsedMillis) {
        this.elapsedMillis = elapsedMillis;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getAdvice() {
        return this.advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getRecordId() {
        return this.recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getIsPatient() {
        return this.isPatient;
    }

    public void setIsPatient(String isPatient) {
        this.isPatient = isPatient;
    }
}
