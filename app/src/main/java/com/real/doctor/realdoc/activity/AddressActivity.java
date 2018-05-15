package com.real.doctor.realdoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.fragment.AddressDialogFragment;
import com.real.doctor.realdoc.model.AddressBean;
import com.real.doctor.realdoc.model.RecieverBean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressActivity extends AppCompatActivity {

    private final String ADDRESS_CODE = "address_code";

    AddressBean addressBean;


    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.address_details)
    EditText addressDetails;
    @BindView(R.id.reciever)
    EditText reciever;
    @BindView(R.id.address)
    TextView address;

    public void setAddress(String address){
        this.address.setText(address);
    }


    @OnClick(R.id.save_address)
    public void SaveAddress(){

        String receiverStr = reciever.getText().toString();
        String phoneStr = phone.getText().toString();
        String addressStr = address.getText().toString();
        String addressDetailsStr = addressDetails.getText().toString();

        if(!receiverStr.equals("") && !phoneStr.equals("")  && !addressStr.equals("null null null") &&  !addressDetailsStr.equals("")){

            Intent resultIntent = new Intent();
            resultIntent.putExtra("recieverName", receiverStr);
            resultIntent.putExtra("recieverPhone", phoneStr);
            resultIntent.putExtra("recieverProvinceCityDistrict", addressStr);
            resultIntent.putExtra("recieverDetailsAddress", addressDetailsStr);

            setResult(RESULT_OK, resultIntent);
            finish();
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

        addressBean = new AddressBean();

    }
}
