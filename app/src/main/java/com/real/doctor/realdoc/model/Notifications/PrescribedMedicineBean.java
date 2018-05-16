package com.real.doctor.realdoc.model.Notifications;

public class PrescribedMedicineBean {
    private String medicine;
    private String code;

    public PrescribedMedicineBean(){

    }

    public PrescribedMedicineBean(String medicine, String code){
        this.medicine = medicine;
        this.code = code;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



}
