package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZFT on 2018/4/26.
 */

public class ProductBean implements Serializable {
    public String product_id;
    public String product_name;
    public String product_detail;
    public Double product_price;
    public String product_show_pic_url;
    public ArrayList<String> imgs=new ArrayList<String>();
}
