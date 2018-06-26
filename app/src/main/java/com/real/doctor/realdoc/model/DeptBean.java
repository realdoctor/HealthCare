package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZFT on 2018/5/22.
 */

public class DeptBean implements Serializable {
    public String deptCode;
    public String deptName;
    public ArrayList<DeptBean> deptList=new ArrayList<DeptBean>();
}
