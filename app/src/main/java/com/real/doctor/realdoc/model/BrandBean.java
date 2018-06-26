package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZFT on 2018/5/3.
 */

public class BrandBean implements Serializable {
    public String breadId;
    public String breadName;
    public ArrayList<ProductInfo> productList =new ArrayList<ProductInfo>();
}
