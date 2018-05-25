package com.real.doctor.realdoc.model;

import java.io.Serializable;

/**
 * Created by ZFT on 2018/5/18.
 */

public class HospitalLevelBean implements Serializable {
    public  String LevelName;
    public int hospitalLevelId;
    //本地的选中标记
    boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
