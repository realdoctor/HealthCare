package com.real.doctor.realdoc.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
@Entity
public class RecieverAddressListBean implements Parcelable {
    @Id(autoincrement = true)
    public Long id;
    public String name;
    public String phone;
    //AddressBean address;
    public String addressStr;
    public String daddress;


    protected RecieverAddressListBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        phone = in.readString();
        addressStr = in.readString();
        daddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(addressStr);
        dest.writeString(daddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecieverAddressListBean> CREATOR = new Creator<RecieverAddressListBean>() {
        @Override
        public RecieverAddressListBean createFromParcel(Parcel in) {
            return new RecieverAddressListBean(in);
        }

        @Override
        public RecieverAddressListBean[] newArray(int size) {
            return new RecieverAddressListBean[size];
        }
    };

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
    @Generated(hash = 1800928767)
    public RecieverAddressListBean() {
    }

    @Generated(hash = 545535503)
    public RecieverAddressListBean(Long id, String name, String phone, String addressStr, String daddress) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.addressStr = addressStr;
        this.daddress = daddress;
    }

    public void setDaddress(String daddress) {
        this.daddress = daddress;
    }

    public String getAddressStr() {
        return addressStr;
    }

    public void setAddressStr(String addressStr) {
        this.addressStr = addressStr;
    }


    public String getDaddress() {
        return daddress;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
