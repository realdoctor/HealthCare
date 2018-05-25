package com.real.doctor.realdoc.model;

import java.io.Serializable;

/**
 * Created by ZFT on 2018/5/18.
 */

/**
 * "doctorIntro": null,
 "doctorName": null,
 "goodAt": null,
 "hospitalId": 406,
 "hospitalLevel": "三级",
 "hospitalName": "广医四院",
 "lat": "22.949391",
 "lng": "113.493567",
 "markNum": "0",
 "positional": null
 */

public class HospitalBean implements Serializable {
    public String doctorIntro;
    public String doctorName;
    public String goodAt;
    public String hospitalId;
    public String hospitalLevel;
    public String hospitalName;
    public String lat;
    public String lng;
    public String markNum;
    public String positional;
    public String hospitalImage="https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3426565566,2670527994&fm=27&gp=0.jpg";
}
