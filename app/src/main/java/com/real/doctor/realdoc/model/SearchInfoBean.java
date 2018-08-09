package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SearchInfoBean implements Parcelable {
    @Id
    private String value;
    private String cate;

    protected SearchInfoBean(Parcel in) {
        value = in.readString();
        cate = in.readString();
    }

    @Generated(hash = 1188685665)
    public SearchInfoBean(String value, String cate) {
        this.value = value;
        this.cate = cate;
    }

    @Generated(hash = 185636360)
    public SearchInfoBean() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeString(cate);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public static final Creator<SearchInfoBean> CREATOR = new Creator<SearchInfoBean>() {
        @Override
        public SearchInfoBean createFromParcel(Parcel in) {
            return new SearchInfoBean(in);
        }

        @Override
        public SearchInfoBean[] newArray(int size) {
            return new SearchInfoBean[size];
        }
    };
}
