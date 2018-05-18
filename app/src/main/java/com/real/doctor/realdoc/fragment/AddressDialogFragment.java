package com.real.doctor.realdoc.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.EditAddressActivity;
import com.real.doctor.realdoc.model.AddressBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddressDialogFragment extends DialogFragment {

    Unbinder unbinder;

    @BindView(R.id.addresses_rv) RecyclerView addresses_rv;

    @BindView(R.id.address_dialog_title) TextView addressDialogTitle;
    @BindView(R.id.province) TextView address_province;
    @BindView(R.id.city) TextView address_city;
    @BindView(R.id.district) TextView address_district;

    AddressBean addressBean;
    AddressDialogAdapter mAddressDialogAdapter;

    List<JSONObject> provincesObjectList;
    List<String> provinces;
    List<String> cities;
    List<String> districts;

    Boolean isProvinceSelected;
    Boolean isCitySelected;

    Boolean firstTimeShowProvinceList;
    Boolean firstTimeShowCityList;


    Boolean isProvinceScreenSelected;
    Boolean isCityScreenSelected;
    Boolean isDistrictScreenSelected;

    String selectedProvince = "";
    String selectedCity= "";
    int selected_position = -1;


    @OnClick(R.id.close_dialog)
    void closeDialog(){
        dismiss();
    }

    @OnClick(R.id.province)
    void showProvinceList(){
        // args: province, city, district
        screenSwitch(true, false, false);
    }

    @OnClick(R.id.city)
    void showCityList(){
        // args: province, city, district
        screenSwitch(false, true, false);
    }

    @OnClick(R.id.district)
    void showDistrictList(){
        // args: province, city, district
        screenSwitch(false, false, true);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_address, null);
        unbinder = ButterKnife.bind(this, view);

        // Lists
        provincesObjectList = new ArrayList<>();
        provinces = new ArrayList<>();
        cities = new ArrayList<>();
        districts = new ArrayList<>();
        addressBean = new AddressBean();

        // Boolean values
        firstTimeShowProvinceList = true;
        firstTimeShowCityList = true;

        isProvinceScreenSelected = true;
        isCityScreenSelected = false;
        isDistrictScreenSelected = false;

        isProvinceSelected = true;
        isCitySelected = false;


        // RecyclerView
        addresses_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddressDialogAdapter = new AddressDialogAdapter();
        addresses_rv.setAdapter(mAddressDialogAdapter);

        populateProvinceList();

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        // dialog properties
        Window window = alertDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        return alertDialog;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /** Populate lists  **/
    public void populateProvinceList(){

        String jsonString  = parseJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject provincesObject = jsonArray.getJSONObject(i);
                provincesObjectList.add(provincesObject);

                Iterator<String> iter = provincesObject.keys();
                String provinceName = iter.next();

                provinces.add(provinceName);

            }

        } catch (Exception e){
             Log.e("populateProvinceList: ", e.getMessage());
        }
    }

    public void populateCitiesList(String province, int position){

        cities.clear();

        // get the list of cities according to the selected province:
        try {
            int numberOfCities = provincesObjectList.get(position).getJSONArray(province).length();
            for (int j = 0; j < numberOfCities; j++) {
                try {
                    // get each city object of the selected province
                    JSONObject cityObject = provincesObjectList.get(position).getJSONArray(province).getJSONObject(j);

                    // get the name of the city and add it to the list
                    String cityName = cityObject.names().get(0).toString();
                    cities.add(cityName);

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
            }

        } catch (JSONException e) {
            Log.e("Error out", e.getMessage());
            e.printStackTrace();

        }

    }

    public void populateDistrictList(String province, String city, int position){

        districts.clear();

        try {
            // get district object according to the selected province and city:
            JSONArray districtObject = provincesObjectList.get(provinces.indexOf(addressBean.getProvince())).getJSONArray(province).getJSONObject(position).getJSONArray(city);

            // add district names to the list
            for (int j = 0; j < districtObject.length(); j++) {
                districts.add(districtObject.get(j).toString());
            }

        } catch (Exception e) {
            Log.e("Error select", e.getMessage());
        }
    }

    /** Display data **/
    public void displayProvince(RecyclerView.ViewHolder holder, final int position){

        final ProvinceHolder provinceHolder = (ProvinceHolder) holder;
        provinceHolder.address_line_text.setText(provinces.get(position));

        // when the dialog is shown for the first time select the first province as the default value:
        if(isFirstTimeScreen(isProvinceScreenSelected, firstTimeShowProvinceList)) {

            //select the first province in the list
            setFirstDefaultItemSelected(provinceHolder, "province");

            // add the cities of the first default province to the list
            populateCitiesList(provinces.get(0), 0);

            // don't let add the first province anymore
            firstTimeShowProvinceList = false;
        }

        // select single item
        singleSelectItem(provinceHolder, position);

        // set click listener to the whole province row
        ConstraintLayout constraintLayout = (ConstraintLayout) provinceHolder.itemView;
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // save the selected province name
                addressBean.setProvince(provinces.get(position));

                // update the selected item position for a single select
                updateSelectedPosition(provinceHolder);

                // don't display the default cities of the first province anymore
                firstTimeShowCityList = false;

                populateCitiesList(addressBean.getProvince(), position);

            }
        });
    }

    public void displayCity(RecyclerView.ViewHolder holder, final int position){

        final CityHolder cityHolder = (CityHolder) holder;
        cityHolder.address_line_text.setText(cities.get(position));

        // when the dialog is shown for the first time select the first city as the default value:
        if(isFirstTimeScreen(isCityScreenSelected, firstTimeShowCityList)){

            setFirstDefaultItemSelected(cityHolder, "city");

            // populate the district list according to the default province and city (Beijing, Beijing):
            populateDistrictList(addressBean.getProvince(), addressBean.getCity(), 0);

            // don't let add the first city anymore
            firstTimeShowCityList = false;

        } else {

            // select single item
            singleSelectItem(cityHolder, position);

            selectedProvince = addressBean.getProvince();

            // let user choose the city if it is not the only one in the list
            if (cities.size() > 1) {

                // implement click on the city
                ConstraintLayout cityConstraintLayout = (ConstraintLayout) cityHolder.itemView;
                cityConstraintLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // update the selected item position for a single select
                        updateSelectedPosition(cityHolder);

                        // save the selected city name
                        addressBean.setCity(cities.get(position));

                        // populate the district list according to the selected province and city
                        populateDistrictList(selectedProvince, addressBean.getCity(), position);

                    }
                });
            }
            // auto-select the city if it is the only one in a province
            else {
                // select the city
                setFirstDefaultItemSelected(cityHolder, "city");

                // save the selected city name
                selectedCity = addressBean.getCity();

                populateDistrictList(selectedProvince, selectedCity, position);

            }
        }

    }

    public void displayDistrict(RecyclerView.ViewHolder holder, final int position){

        DistrictHolder districtHolder = (DistrictHolder) holder;
        districtHolder.address_line_text.setText(districts.get(position));

        // don't set the default value - user will have to choose it by himself
        addressBean.setDistrict("");

        ConstraintLayout cityConstraintLayout = (ConstraintLayout) districtHolder.itemView;
        cityConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setSelected(true);
                addressBean.setDistrict(districts.get(position));

                // set the value of the address from the EditAddressActivity to the final values: province city district
                ((EditAddressActivity)getActivity()).setAddress(addressBean.getProvince() + " " + addressBean.getCity() + " " + addressBean.getDistrict());
                ((EditAddressActivity) getActivity()).setAddressBean(addressBean);
                // close the dialog as soon as user selects the district
                dismiss();

            }
        });
    }


    /** Holders classes **/
    public class GeneralHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.address_line_text) TextView address_line_text;
        @BindView(R.id.address_line_selected) ImageView address_line_selected;

        public GeneralHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public class ProvinceHolder extends GeneralHolder{
        public ProvinceHolder(View itemView) {
            super(itemView);
        }
    }

    public class CityHolder extends GeneralHolder{
        public CityHolder(View itemView) {
            super(itemView);
        }
    }

    public class DistrictHolder extends GeneralHolder{
        public DistrictHolder(View itemView) {
            super(itemView);
        }
    }


    /** Adapter **/
    public class AddressDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.address_view, parent, false);

            if(whichScreenSelected() == "province")
                return new ProvinceHolder(view);
            else if(whichScreenSelected() == "city")
                return new CityHolder(view);
            else
                return new DistrictHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

            // Province screen:
            if(holder instanceof ProvinceHolder) {
                displayProvince(holder, position);
            }
            // City screen
            else if (holder instanceof CityHolder) {
                displayCity(holder, position);
            }
            // District screen
            else if(holder instanceof DistrictHolder) {
                displayDistrict(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            if(isProvinceScreenSelected)
                return provinces.size();
            else if(isCityScreenSelected)
                return cities.size();

            return districts.size();
        }
    }


    /** Utility functions **/

    public void setFirstDefaultItemSelected(RecyclerView.ViewHolder holder, String screen){

        holder.itemView.setSelected(true);

        switch (screen){
            case "province": addressBean.setProvince(provinces.get(0));
                break;
            case "city": addressBean.setCity(cities.get(0));
                break;
            case "district": addressBean.setDistrict(districts.get(0));
                break;
        }

    }

    public void  singleSelectItem(RecyclerView.ViewHolder holder, int position){

        if(position == selected_position){
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }
    }

    public void updateSelectedPosition(RecyclerView.ViewHolder holder){

        selected_position = holder.getAdapterPosition();
        mAddressDialogAdapter.notifyDataSetChanged();
    }

    public Boolean isFirstTimeScreen(Boolean selectedScreen, Boolean firstTime){

        if(selectedScreen && firstTime)
            return true;

        return false;
    }

    public String whichScreenSelected(){

        if(isProvinceScreenSelected) {
            return "province";
        } else if (isCityScreenSelected) {
            return "city";
        } else if(isDistrictScreenSelected) {
            return "district";
        }

        return "none";
    }

    public void screenSwitch(Boolean provinceScreen, Boolean cityScreen, Boolean districtScreen){

        isProvinceScreenSelected = provinceScreen;
        isCityScreenSelected = cityScreen;
        isDistrictScreenSelected = districtScreen;

        // show the list according to the clicked screen: province, city or district
        addresses_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAddressDialogAdapter = new AddressDialogAdapter();
        addresses_rv.setAdapter(mAddressDialogAdapter);

    }

    public String parseJSON(){

        InputStream is = getResources().openRawResource(R.raw.address);
        StringBuilder buffer = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

        } catch (Exception e) {
            Log.e("Error :", e.getMessage());
        } finally
        {
            try {
                is.close();
                reader.close();
            } catch (Exception e){
                Log.e("Error :", e.getMessage());
            }
        }

        String jsonString = buffer.toString();

        return  jsonString;

    }


}