package com.real.doctor.realdoc.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ZFT on 2018/6/1.
 */

public class OrderModel implements Serializable {
    public String addTime;
    public String closeTime;
    public String consignTime;
    public String goodsOrderId;
    public String orderNo;
    public String payPrice;
    public String payTime;
    public String receiptTime;
    public String sentStatus;
    public String shippingCode;
    public String shippingName;
    public String tradeStatus;
    public String userId;
    public ArrayList<OrderDetailModel> orderList=new ArrayList<OrderDetailModel>();
}
