package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class InqueryBean implements Parcelable {
    @Id(autoincrement = true)
    private Long id;
    private String inquery;
    private String answer;

    public InqueryBean() {
    }

    protected InqueryBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        inquery = in.readString();
        answer = in.readString();
    }

    @Generated(hash = 1763238806)
    public InqueryBean(Long id, String inquery, String answer) {
        this.id = id;
        this.inquery = inquery;
        this.answer = answer;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(inquery);
        dest.writeString(answer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInquery() {
        return this.inquery;
    }

    public void setInquery(String inquery) {
        this.inquery = inquery;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public static final Creator<InqueryBean> CREATOR = new Creator<InqueryBean>() {
        @Override
        public InqueryBean createFromParcel(Parcel in) {
            return new InqueryBean(in);
        }

        @Override
        public InqueryBean[] newArray(int size) {
            return new InqueryBean[size];
        }
    };


}
