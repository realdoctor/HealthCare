package com.real.doctor.realdoc.util;


import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.SortBean;

/**
 * 点击筛选数据的保存
 */
public class FilterUtils {

    private volatile static FilterUtils filterUtils;

    private FilterUtils() {
    }

    public static FilterUtils instance() {
        if (filterUtils == null) {
            synchronized (FilterUtils.class) {
                if (filterUtils == null) {
                    filterUtils = new FilterUtils();
                }
            }
        }
        return filterUtils;
    }
    //所有的某个位置的标题
    public int position;
    public String sortTitle;
    public String HosptialLevelTitle;

    public String singleListPosition;


    //5个gridView的数值
    public HospitalLevelBean multiGirdOne;
    public SortBean multiGirdTwo;
    public ExpertPostionalBean multiGirdThree;
    public void clear() {
        filterUtils = null;
    }


}