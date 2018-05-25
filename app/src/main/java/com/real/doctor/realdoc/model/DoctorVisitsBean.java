package com.real.doctor.realdoc.model;

import android.graphics.Bitmap;

import java.util.Date;

public class DoctorVisitsBean {
    private Bitmap doctorImg;
    private String doctorName;
    private String hospitalName;
    private String deseaseName;
    private Date visitTime;

    public DoctorVisitsBean(){ }


    public DoctorVisitsBean(String doctorName, String hospitalName, String deseaseName, Date visitTime) {
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
        this.deseaseName = deseaseName;
        this.visitTime = visitTime;
    }


    public Bitmap getDoctorImg() {
        return doctorImg;
    }

    public void setDoctorImg(Bitmap doctorImg) {
        this.doctorImg = doctorImg;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDeseaseName() {
        return deseaseName;
    }

    public void setDeseaseName(String deseaseName) {
        this.deseaseName = deseaseName;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }



}
