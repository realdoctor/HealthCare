package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 多筛选条件
 */

public class FilterBean implements Serializable {
    public ArrayList<SortBean> sortList;
    public ArrayList<HospitalLevelBean> hospitalLevelBeans;
    public ArrayList<ExpertPostionalBean> expertPostionalBeans;

    public ArrayList<SortBean> getSortList() {
        return sortList;
    }

    public void setSortList(ArrayList<SortBean> sortList) {
        this.sortList = sortList;
    }

    public ArrayList<HospitalLevelBean> getHospitalLevelBeans() {
        return hospitalLevelBeans;
    }

    public void setHospitalLevelBeans(ArrayList<HospitalLevelBean> hospitalLevelBeans) {
        this.hospitalLevelBeans = hospitalLevelBeans;
    }

    public ArrayList<ExpertPostionalBean> getExpertPostionalBeans() {
        return expertPostionalBeans;
    }

    public void setExpertPostionalBeans(ArrayList<ExpertPostionalBean> expertPostionalBeans) {
        this.expertPostionalBeans = expertPostionalBeans;
    }
}
