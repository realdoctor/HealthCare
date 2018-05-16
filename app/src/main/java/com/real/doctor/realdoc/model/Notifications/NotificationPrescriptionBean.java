package com.real.doctor.realdoc.model.Notifications;

import java.util.List;

public class NotificationPrescriptionBean extends NotificationBean {
    private List<PrescribedMedicineBean> prescriptions;

    public NotificationPrescriptionBean(){

    }

    public NotificationPrescriptionBean(List<PrescribedMedicineBean> prescriptions){
        this.prescriptions = prescriptions;
    }

    public List<PrescribedMedicineBean> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<PrescribedMedicineBean> prescriptions) {
        this.prescriptions = prescriptions;
    }


}
