package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZFT on 2018/5/3.
 */

public class CategoryBean implements Serializable {
    public String categoryId;
    public String categoryName;
    public ArrayList<BrandBean> brands=new ArrayList<BrandBean>();
}
