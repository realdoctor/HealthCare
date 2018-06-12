package com.real.doctor.realdoc.model;

import android.os.Parcelable;

public class PatientBean extends UserBean implements Parcelable {
    //相当于id,到时候直接用id
    private String patientCode;
    //    private String patientImg;
    private String visitOrgName;
    private String diagName;
    private String visitDtime;

    public PatientBean() {
    }

    public String getPatientCode() {
        return patientCode;
    }

    public void setPatientCode(String patientCode) {
        this.patientCode = patientCode;
    }

    public String getVisitOrgName() {
        return visitOrgName;
    }

    public void setVisitOrgName(String visitOrgName) {
        this.visitOrgName = visitOrgName;
    }

    public String getDiagName() {
        return diagName;
    }

    public void setDiagName(String diagName) {
        this.diagName = diagName;
    }

    public String getVisitDtime() {
        return visitDtime;
    }

    public void setVisitDtime(String visitDtime) {
        this.visitDtime = visitDtime;
    }
}
