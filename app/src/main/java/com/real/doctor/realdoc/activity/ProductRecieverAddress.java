package com.real.doctor.realdoc.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.AddressBean;
import com.real.doctor.realdoc.model.RecieverBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductRecieverAddress extends AppCompatActivity {

    final private static int CHANGE_ADDRESS_CODE = 1;
    private RecieverBean mRecieverBean;

    private Boolean objectExistsFlag;

    private String recieverName;
    private String recieverPhone;
    private String recieverProvinceCityDistrict;
    private String recieverDetailsAddress;

    @BindView(R.id.product_reciever_name)
    TextView productRecieverName;

    @BindView(R.id.product_reciever_phone)
    TextView productRecieverPhone;

    @BindView(R.id.product_reciever_full_address)
    TextView productRecieverFullAddress;

    @OnClick(R.id.change_product_reciever_address)
    void changeAddress(){
        Intent intent = new Intent (this, AddressActivity.class);

        Log.e("changeAddress",   "yes");

        if(objectExistsFlag){
            intent.putExtra("recieverName", mRecieverBean.getName());
            intent.putExtra("recieverPhone", mRecieverBean.getPhoneNumber());
            intent.putExtra("recieverProvinceCityDistrict", mRecieverBean.getProvinceCityDistrict());
            intent.putExtra("recieverDetailsAddress", mRecieverBean.getAddress_details());
        }


        startActivityForResult(intent, CHANGE_ADDRESS_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_reciever_address);
        ButterKnife.bind(this);

        Log.e("onCreate",   "yes");
        mRecieverBean = new RecieverBean();

        if(!mRecieverBean.isNullObject()){
            objectExistsFlag = false;
            changeAddress();
        } else {
            objectExistsFlag = true;

            recieverName = mRecieverBean.getName();
            recieverPhone = mRecieverBean.getPhoneNumber();
            recieverProvinceCityDistrict = mRecieverBean.getProvinceCityDistrict();
            recieverDetailsAddress = mRecieverBean.getAddress_details();

            setData(recieverName, recieverPhone, recieverProvinceCityDistrict + recieverDetailsAddress);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            recieverName = data.getStringExtra("recieverName");
            recieverPhone = data.getStringExtra("recieverPhone");
            recieverProvinceCityDistrict = data.getStringExtra("recieverProvinceCityDistrict");
            recieverDetailsAddress = data.getStringExtra("recieverDetailsAddress");

            mRecieverBean.setName(recieverName);
            mRecieverBean.setPhoneNumber(recieverPhone);
            mRecieverBean.setProvinceCityDistrict(recieverProvinceCityDistrict);
            mRecieverBean.setAddress_details(recieverDetailsAddress);

            setData(recieverName, recieverPhone, recieverProvinceCityDistrict + recieverDetailsAddress);
        }

    }

    public void setData(String name, String phone, String address){

        productRecieverName.setText(name);
        productRecieverPhone.setText(phone);
        productRecieverFullAddress.setText(address);
    }
}
