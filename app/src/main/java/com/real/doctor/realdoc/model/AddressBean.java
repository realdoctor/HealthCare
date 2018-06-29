package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AddressBean implements Parcelable {
    private  String province;
    private  String city;
    private  String district;

    private String provinceCityDistrict;
    private String streetDetails;

    public AddressBean(){ }


    public AddressBean(String province, String city, String district, String streetDetails){
        this.province = province;
        this. city = city;
        this.district = district;
        this.streetDetails = streetDetails;
    }


    protected AddressBean(Parcel in) {
        province = in.readString();
        city = in.readString();
        district = in.readString();
        provinceCityDistrict = in.readString();
        streetDetails = in.readString();
    }

    public static final Creator<AddressBean> CREATOR = new Creator<AddressBean>() {
        @Override
        public AddressBean createFromParcel(Parcel in) {
            return new AddressBean(in);
        }

        @Override
        public AddressBean[] newArray(int size) {
            return new AddressBean[size];
        }
    };

    public  String getProvince() {
        return province;
    }

    public  void setProvince(String province) {
        this.province = province;
    }

    public  String getCity() {
        return city;
    }

    public  void setCity(String city) {
        this.city = city;
    }

    public  String getDistrict() {
        return district;
    }

    public  void setDistrict(String district) {
        this.district = district;
    }


    public void setProvinceCityDistrict(String provinceCityDistrict){
        this.provinceCityDistrict = provinceCityDistrict;
    }

    public String getProvinceCityDistrict(){
        return provinceCityDistrict;
    }

    public String getStreetDetails() {
        return streetDetails;
    }

    public void setStreetDetails(String streetDetails) {
        this.streetDetails = streetDetails;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(province);
        dest.writeString(city);
        dest.writeString(district);
        dest.writeString(provinceCityDistrict);
        dest.writeString(streetDetails);
    }
}
