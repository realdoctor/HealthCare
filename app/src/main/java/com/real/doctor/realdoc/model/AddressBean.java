package com.real.doctor.realdoc.model;

public class AddressBean {
    private static String province;
    private static String city;
    private static String district;

    public AddressBean(){

    }

    public AddressBean(String province, String city, String district){
        this.province = province;
        this. city = city;
        this.district = district;
    }


    public static String getProvince() {
        return province;
    }

    public static void setProvince(String province) {
        AddressBean.province = province;
    }

    public static String getCity() {
        return city;
    }

    public static void setCity(String city) {
        AddressBean.city = city;
    }

    public static String getDistrict() {
        return district;
    }

    public static void setDistrict(String district) {
        AddressBean.district = district;
    }

}
