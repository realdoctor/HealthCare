package com.real.doctor.realdoc.util;

import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.OrderStatusModel;
import com.real.doctor.realdoc.model.SortBean;

import java.util.ArrayList;

/**
 */

public class DataUtil {
    public static ArrayList<SortBean> sortBeans=new ArrayList<SortBean>();
    public static ArrayList<HospitalLevelBean> hospitalLevelBeans=new ArrayList<HospitalLevelBean>();
    public static ArrayList<ExpertPostionalBean> expertPostionalBeans=new ArrayList<ExpertPostionalBean>();
    public static ArrayList<OrderStatusModel> orderStatusModels=new ArrayList<OrderStatusModel>();
    public static ArrayList<BrandBean> brandBeans=new ArrayList<BrandBean>();
    static {
        SortBean bean0=new SortBean();
        bean0.SortId="0";
        bean0.sortName="离我最近";
        sortBeans.add(bean0);
        SortBean bean=new SortBean();
        bean.SortId="1";
        bean.sortName="综  合";
        sortBeans.add(bean);
        SortBean bean2=new SortBean();
        bean2.SortId="3";
        bean2.sortName="预约量";
        sortBeans.add(bean2);
        HospitalLevelBean hBean0=new HospitalLevelBean();
        hBean0.LevelName="不限";
        HospitalLevelBean hBean=new HospitalLevelBean();
        hBean.LevelName="一级甲等";
        HospitalLevelBean hBean2=new HospitalLevelBean();
        hBean2.LevelName="二级甲等";
        HospitalLevelBean hBean3=new HospitalLevelBean();
        hBean3.LevelName="三级甲等";
        hospitalLevelBeans.add(hBean0);
        hospitalLevelBeans.add(hBean);
        hospitalLevelBeans.add(hBean2);
        hospitalLevelBeans.add(hBean3);
        ExpertPostionalBean ebean0=new ExpertPostionalBean();
        ebean0.postional="不限";
        expertPostionalBeans.add(ebean0);
        ExpertPostionalBean ebean=new ExpertPostionalBean();
        ebean.postional="主任";
        expertPostionalBeans.add(ebean);
        ExpertPostionalBean ebean2=new ExpertPostionalBean();
        ebean2.postional="副主任";
        expertPostionalBeans.add(ebean2);
        OrderStatusModel model=new OrderStatusModel();
        model.orderstatus="1";
        model.order_desc="未支付";
        OrderStatusModel model1=new OrderStatusModel();
        model1.orderstatus="2";
        model1.order_desc="已支付";
        OrderStatusModel model2=new OrderStatusModel();
        model2.orderstatus="3";
        model2.order_desc="交易关闭";
        orderStatusModels.add(model);
        orderStatusModels.add(model1);
        orderStatusModels.add(model2);

        BrandBean brandBean=new BrandBean();
        brandBean.breadName="感康";
        brandBeans.add(brandBean);
        BrandBean brandBean2=new BrandBean();
        brandBean2.breadName="三九";
        brandBeans.add(brandBean2);


    }
}
