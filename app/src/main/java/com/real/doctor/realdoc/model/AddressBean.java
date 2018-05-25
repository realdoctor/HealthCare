package com.real.doctor.realdoc.model;

public class AddressBean {
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




}
