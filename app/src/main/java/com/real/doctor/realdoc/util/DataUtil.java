package com.real.doctor.realdoc.util;

import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;

import java.util.ArrayList;

/**
 */

public class DataUtil {
    public static ArrayList<SortBean> sortBeans=new ArrayList<SortBean>();
    public static ArrayList<HospitalLevelBean> hospitalLevelBeans=new ArrayList<HospitalLevelBean>();
    static {
        SortBean bean=new SortBean();
        bean.SortId="1";
        bean.sortName="综  合";
        sortBeans.add(bean);
        SortBean bean2=new SortBean();
        bean2.SortId="3";
        bean2.sortName="预约量";
        sortBeans.add(bean2);
        HospitalLevelBean hBean=new HospitalLevelBean();
        hBean.LevelName="一级甲等";
        HospitalLevelBean hBean2=new HospitalLevelBean();
        hBean2.LevelName="二级甲等";
        HospitalLevelBean hBean3=new HospitalLevelBean();
        hBean3.LevelName="三级甲等";
        hospitalLevelBeans.add(hBean);
        hospitalLevelBeans.add(hBean2);
        hospitalLevelBeans.add(hBean3);

    }
}
