package com.real.doctor.realdoc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;

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

public class AppointmentAddressActivity extends AppCompatActivity {

    private AppointmentAddressAdapter mAppointmentAddressAdapter;

    @BindView(R.id.appointment_province_list)
    RecyclerView appointment_province_list;

    @BindView(R.id.appointment_city_list)
    RecyclerView appointment_city_list;

    @BindView(R.id.selectedAddressHeader)
    TextView selectedAddressHeader;

    @OnClick(R.id.cancel)
    void cancel() {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);

        finish();
    }

    // Data
    private List<JSONObject> provincesObjectList;
    private List<String> provinces;
    private List<String> cities;

    private int selectedProvincePosition = -1;
    private String selectedProvince = "";
    private String selectedCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_address);
        ButterKnife.bind(this);

        provincesObjectList = new ArrayList<>();
        initData();

        // adapter for province list
        appointment_province_list.setLayoutManager(new LinearLayoutManager(this));
        mAppointmentAddressAdapter = new AppointmentAddressAdapter(R.layout.appointment_address_listitem_view, provinces, "province");
        appointment_province_list.setAdapter(mAppointmentAddressAdapter);

        // onclick listener for province
        mAppointmentAddressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                // reset previous data:
                selectedAddressHeader.setText("");
                selectedCity = "";

                // set the selected province:
                selectedProvince = provinces.get(position);
                selectedAddressHeader.setText(selectedProvince);


                updateSelectedPosition(position, "province");

                // populate cities according to selected province
                cities = new ArrayList<>();
                populateCitiesList(provinces.get(position), position);

                // set adapter to cities list
                appointment_city_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new AppointmentAddressAdapter(R.layout.appointment_address_listitem_view, cities, "city");
                appointment_city_list.setAdapter(adapter);

                // onclick listener to city
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        // set selected city
                        selectedCity = cities.get(position);

                        // don't show 热门地区 in the header, show only the city name
                        if (selectedProvince.equals("热门地区")) {
                            selectedAddressHeader.setText(selectedCity);
                        } else {
                            selectedAddressHeader.setText(selectedProvince + selectedCity);
                        }
                        // return selected province and city
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("city", selectedCity);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
            }
        });
    }

    public void initData() {
        provinces = new ArrayList<>();
        populateProvinceList();

    }


    public class AppointmentAddressAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        List<String> data;
        String selectedListflag;

        public AppointmentAddressAdapter(int layoutResId, @Nullable List<String> data, String flag) {
            super(layoutResId, data);

            this.data = data;
            this.selectedListflag = flag;
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {

            helper.setText(R.id.appointment_address_text, item + "");

            // select one item at a time in the province list
            if (selectedListflag.equals("province"))
                singleSelectItem(helper.itemView, data.indexOf(item), selectedProvincePosition);

        }

    }


    /**
     * Populate lists
     **/
    public void populateProvinceList() {

        // manually add 热门地区 to province list
        provinces.add("热门地区");

        String jsonString = parseJSON();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            // Log.e("jsonArray: ", jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject provincesObject = jsonArray.getJSONObject(i);
                // Log.e("provincesObject: ", provincesObject.toString());
                provincesObjectList.add(provincesObject);

                Iterator<String> iter = provincesObject.keys();
                String provinceName = iter.next();

                provinces.add(provinceName);

            }

        } catch (Exception e) {
            Log.e("populateProvinceList: ", e.getMessage());
        }
    }

    public void populateCitiesList(String province, int position) {

        cities.clear();

        // manually populate 热门地区 cities
        if (province.equals("热门地区")) {
            cities.add("全国");
            cities.add("上海");
            cities.add("北京");
            cities.add("广州");
            cities.add("深圳");
            cities.add("武汉");
            cities.add("杭州");
            cities.add("长沙");
            cities.add("南京");
            cities.add("重庆");
        } else {

            // get the list of cities according to the selected province:
            try {
                /** delete when 热门地区 cities will be got from API **/
                position = position - 1;

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
    }


    /**
     * Utility functions
     **/
    public String parseJSON() {

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
        } finally {
            try {
                is.close();
                reader.close();
            } catch (Exception e) {
                Log.e("Error :", e.getMessage());
            }
        }

        String jsonString = buffer.toString();

        return jsonString;

    }


    public void singleSelectItem(View view, int position, int selectedPosition) {

        if (position == selectedPosition) {
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            view.setBackgroundColor(Color.parseColor("#E2EEE9"));
        }


    }

    public void updateSelectedPosition(int position, String selectedRow) {

        switch (selectedRow) {
            case "province":
                selectedProvincePosition = position;
                break;

        }

        mAppointmentAddressAdapter.notifyDataSetChanged();
    }
}
