package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class VideoBean implements Parcelable {

    @Id
    private String fileName;
    private String filePath;
    private String elapsedMillis;
    private String date;
    private String folder;
    private String recordId;

    protected VideoBean(Parcel in) {
        fileName = in.readString();
        filePath = in.readString();
        elapsedMillis = in.readString();
        date = in.readString();
    }

    @Generated(hash = 915646413)
    public VideoBean(String fileName, String filePath, String elapsedMillis,
            String date, String folder, String recordId) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.elapsedMillis = elapsedMillis;
        this.date = date;
        this.folder = folder;
        this.recordId = recordId;
    }

    @Generated(hash = 2024490299)
    public VideoBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(filePath);
        dest.writeString(elapsedMillis);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public String getRecordId() {
        return this.recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };
}
