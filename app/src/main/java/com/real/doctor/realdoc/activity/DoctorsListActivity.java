package com.real.doctor.realdoc.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.DoctorVisitsBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoctorsListActivity extends AppCompatActivity {

    @BindView(R.id.doctors_list_rv)
    RecyclerView doctors_list_rv;

    DoctorListAdapter mDoctorListAdapter;
    List<DoctorVisitsBean> visits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_list);
        ButterKnife.bind(this);

        visits = new ArrayList<>();
        initData();


        doctors_list_rv.setLayoutManager(new LinearLayoutManager(this));
        mDoctorListAdapter = new DoctorListAdapter(R.layout.doctors_list_item_view, visits);
        doctors_list_rv.setAdapter(mDoctorListAdapter);


    }

    public void initData(){

        for(int i = 0; i < 10; i ++){
            DoctorVisitsBean bean = new DoctorVisitsBean();
            bean.setDoctorName("Name" + i);
            bean.setHospitalName("Hospital name " + i);
            bean.setDeseaseName("Desease name " + i);

            Date date = new Date();
            date.getTime();
            bean.setVisitTime(date);

            visits.add(bean);
        }
    }



    public class DoctorListAdapter extends BaseQuickAdapter<DoctorVisitsBean, BaseViewHolder>{

        public DoctorListAdapter(int layoutResId, @Nullable List<DoctorVisitsBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DoctorVisitsBean item) {
                helper.setText(R.id.doctor_name, item.getDoctorName())
                        .setText(R.id.hospital_name, item.getHospitalName())
                        .setText(R.id.desease_name, item.getDeseaseName())
                        .setText(R.id.visit_date, item.getVisitTime().toString());
        }
    }
}
