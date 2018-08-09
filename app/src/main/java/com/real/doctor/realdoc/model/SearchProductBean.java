package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class SearchProductBean implements Parcelable {
    @Id
    private String value;
    private String cate;

    protected SearchProductBean(Parcel in) {
        value = in.readString();
        cate = in.readString();
    }

    @Generated(hash = 1478866791)
    public SearchProductBean(String value, String cate) {
        this.value = value;
        this.cate = cate;
    }

    @Generated(hash = 1699612783)
    public SearchProductBean() {
    }

    public static final Creator<SearchProductBean> CREATOR = new Creator<SearchProductBean>() {
        @Override
        public SearchProductBean createFromParcel(Parcel in) {
            return new SearchProductBean(in);
        }

        @Override
        public SearchProductBean[] newArray(int size) {
            return new SearchProductBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeString(cate);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCate() {
        return this.cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

}
