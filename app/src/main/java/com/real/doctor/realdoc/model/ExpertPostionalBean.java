package com.real.doctor.realdoc.model;

import java.io.Serializable;

/**
 * Created by ZFT on 2018/5/25.
 */

public class ExpertPostionalBean implements Serializable{
    public String postional;
    public int id;
    //本地的选中标记
    boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getPostional() {
        return postional;
    }

    public void setPostional(String postional) {
        this.postional = postional;
    }
}
