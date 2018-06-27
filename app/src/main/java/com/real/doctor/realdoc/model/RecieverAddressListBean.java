package com.real.doctor.realdoc.model;

import java.io.Serializable;

public class RecieverAddressListBean implements Serializable {

    private String name;
    private String phone;
    AddressBean address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public AddressBean getAddress() {
        return address;
    }

    public void setAddress(AddressBean address) {
        this.address = address;
    }

    public RecieverAddressListBean(){}

}
