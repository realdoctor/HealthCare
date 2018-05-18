package com.real.doctor.realdoc.model;

import java.util.List;

public class RecieverBean {
    List<RecieverAddressListBean> addresses;

    public RecieverBean(){}

    public List<RecieverAddressListBean> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<RecieverAddressListBean> addresses) {
        this.addresses = addresses;
    }

}
