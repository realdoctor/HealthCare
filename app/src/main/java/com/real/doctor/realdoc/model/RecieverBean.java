package com.real.doctor.realdoc.model;

public class RecieverBean {
    private String name;
    private String phoneNumber;
    private String provinceCityDistrict;
    private String address_details;

    public RecieverBean(){

    }

    public RecieverBean(String name, String phoneNumber, String provinceCityDistrict, String address_details){
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.provinceCityDistrict = provinceCityDistrict;
        this.address_details = address_details;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProvinceCityDistrict() {
        return provinceCityDistrict;
    }

    public void setProvinceCityDistrict(String provinceCityDistrict) {
        this.provinceCityDistrict = provinceCityDistrict;
    }

    public String getAddress_details() {
        return address_details;
    }

    public void setAddress_details(String address_details) {
        this.address_details = address_details;
    }


}
