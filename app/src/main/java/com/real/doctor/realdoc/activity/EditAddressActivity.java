package com.real.doctor.realdoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.fragment.AddressDialogFragment;
import com.real.doctor.realdoc.fragment.OrderExpertByNameFragment;
import com.real.doctor.realdoc.greendao.table.RecieverAddressListManager;
import com.real.doctor.realdoc.model.AddressBean;
import com.real.doctor.realdoc.model.RecieverAddressListBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.real.doctor.realdoc.activity.AddressListActivity.ADD_EVENT_REQUEST_CODE;
import static com.real.doctor.realdoc.activity.AddressListActivity.EDIT_EVENT_REQUEST_CODE;

public class EditAddressActivity extends AppCompatActivity {

    private final String ADDRESS_CODE = "address_code";

    AddressBean addressBean;
    public RecieverAddressListManager addressInstance;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address_details)
    EditText addressDetails;
    @BindView(R.id.reciever)
    EditText reciever;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    public int requestCode;
    RecieverAddressListBean model;
    public void setAddress(String address){
        this.address.setText(address);
    }


    @OnClick(R.id.save_address)
    public void SaveAddress(){

        String receiverStr = reciever.getText().toString();
        String phoneStr = phone.getText().toString();
        String provinceCityDistrictStr = address.getText().toString();
        String addressDetailsStr = addressDetails.getText().toString();

        if(!receiverStr.equals("") && !phoneStr.equals("")  && !provinceCityDistrictStr.equals("null null null") && !addressDetailsStr.equals("")){

            if(requestCode==ADD_EVENT_REQUEST_CODE) {
                RecieverAddressListBean bean = new RecieverAddressListBean();
                bean.setName(receiverStr);
                bean.setPhone(phoneStr);
                bean.setAddressStr(provinceCityDistrictStr);
                bean.setDaddress(addressDetailsStr);
                addressInstance.insertBean(EditAddressActivity.this, bean);
                EditAddressActivity.this.finish();
            }else if(requestCode==EDIT_EVENT_REQUEST_CODE){
                model.setName(receiverStr);
                model.setPhone(phoneStr);
                model.setAddressStr(provinceCityDistrictStr);
                model.setDaddress(addressDetailsStr);
                addressInstance.updateBean(EditAddressActivity.this,model);
                EditAddressActivity.this.finish();
            }
        } else{
            Toast.makeText(this, "Empty fields", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.address)
    public void showDialog(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddressDialogFragment addressDialogFragment = new AddressDialogFragment();
        addressDialogFragment.show(fragmentManager, ADDRESS_CODE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        addressInstance= RecieverAddressListManager.getInstance(EditAddressActivity.this);
        // get data from AddressListActivity
        Intent intent = getIntent();
        finish_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAddressActivity.this.finish();
            }
        });
         requestCode = intent.getIntExtra("requestCode", 0);
        // if edit address - set the data from the previous screen;
        // if add address - leave the fields blank
        if(requestCode == EDIT_EVENT_REQUEST_CODE) {
            model=(RecieverAddressListBean)getIntent().getParcelableExtra("model");
            String nameStr = intent.getStringExtra("name");
            String phoneStr = intent.getStringExtra("phone");
            String provinceCityDistrict = intent.getStringExtra("provinceCityDistrict");
            String streetDetails = intent.getStringExtra("streetDetails");
            reciever.setText(nameStr);
            phone.setText(phoneStr);
            address.setText(provinceCityDistrict);
            addressDetails.setText(streetDetails);
        }

        addressBean = new AddressBean();

    }

    public void setAddressBean(AddressBean addressBean){
        this.addressBean = addressBean;
    }
}
