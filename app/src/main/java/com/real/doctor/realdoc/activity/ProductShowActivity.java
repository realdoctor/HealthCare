package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.youth.banner.Banner;
//import com.youth.banner.Banner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ProductShowActivity extends BaseActivity  {

    @BindView(R.id.banner_id)
    Banner banner;
    @BindView(R.id.tv_pName)
    TextView tv_pName;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_buy)
    TextView goToBuy;
    @BindView(R.id.tv_incart)
    TextView addCart;
    @BindView(R.id.tv_proproty)
    TextView tv_proproty;
    @BindView(R.id.img_cart)
    ImageView cart;
    @BindView(R.id.tv_description)
    TextView description;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.title_bar)
    RelativeLayout topTitle;
    @BindView(R.id.page_title)
    TextView page_title;
    private ProductBean bean;
    private String  userId;
    String goodId;
    int num=1;
    @Override
    public int getLayoutId() {
        return R.layout.activity_product_show;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ProductShowActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
            lp.topMargin = statusHeight;
            topTitle.setLayoutParams(lp);
        }
        page_title.setText("商品详情");
        userId=(String)SPUtils.get(ProductShowActivity.this, Constants.USER_KEY,"");
        bean=(ProductBean) getIntent().getSerializableExtra("model");
        bean.setNum(1);
        banner.setBannerStyle(Banner.CIRCLE_INDICATOR_TITLE);
        banner.setIndicatorGravity(Banner.CENTER);
        banner.isAutoPlay(false) ;
        banner.setImages(new String[]{bean.getBigPic()});
        goodId=bean.getGoodsId();
        tv_pName.setText(bean.getName());
        tv_price.setText("￥"+bean.getCost());
        tv_proproty.setText(bean.getAttribute());
        description.setText(bean.getGoodsDescription());
    }

    @Override
    public void initEvent() {

    }


    @Override
    @OnClick({R.id.tv_buy,R.id.img_cart,R.id.tv_incart,R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.tv_buy:
                if(userId==null||userId.length()==0){
                Toast.makeText(ProductShowActivity.this,"请登录",Toast.LENGTH_SHORT).show();
                return;
                }
                Intent intent =new Intent(ProductShowActivity.this,PayActivity.class);
                intent.putExtra("totalPrice",String.valueOf(bean.getCost()));
                ArrayList<ProductBean> list=new ArrayList<ProductBean>();
                list.add(bean);
                intent.putExtra("goodsList",list);
                startActivity(intent);
                break;
            case R.id.tv_incart:
                if(userId==null||userId.length()==0){
                    Toast.makeText(ProductShowActivity.this,"请登录",Toast.LENGTH_SHORT).show();
                    return;
                }
                addToCart(goodId,num);
                break;
            case R.id.img_cart:
                if(userId==null||userId.length()==0){
                    Toast.makeText(ProductShowActivity.this,"请登录",Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intentCart =new Intent(ProductShowActivity.this,ShopCartActivity.class);
                startActivity(intentCart);
                break;
            case R.id.finish_back:
                ProductShowActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    //添加到购物车
    public void addToCart(String goodsId,int num){
        JSONObject object=new JSONObject();
        try {
            object.put("goodsId",goodsId);
            object.put("num",num);
            object.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(ProductShowActivity.this, "token", "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(ProductShowActivity.this, "请确定您的账户已登录!");
            return;
        }
       HttpRequestClient client= HttpRequestClient.getInstance(ProductShowActivity.this,HttpNetUtil.BASE_URL,header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("cart/addCartItem/"
                , body, new BaseObserver<ResponseBody>(ProductShowActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
                        ToastUtil.showLong(ProductShowActivity.this, "加入购物车失败!");
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    ToastUtil.showLong(ProductShowActivity.this, "加入购物车成功!");
                                } else {
                                    ToastUtil.showLong(ProductShowActivity.this, "加入购物车失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
    //立即购买
    public void payCart(String goodsId,int num){
        JSONObject object=new JSONObject();
        try {
            object.put("goodsId",goodsId);
            object.put("num",num);
            object.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(ProductShowActivity.this, "token", "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(ProductShowActivity.this, "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client= HttpRequestClient.getInstance(ProductShowActivity.this,HttpNetUtil.BASE_URL,header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("cart/addCartItem/"
                , body, new BaseObserver<ResponseBody>(ProductShowActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
                        ToastUtil.showLong(ProductShowActivity.this, "加入购物车失败!");
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    protected void onHandleSuccess(ResponseBody responseBody) {
                        String data = null;
                        String msg = null;
                        String code = null;
                        try {
                            data = responseBody.string().toString();
                            try {
                                JSONObject object = new JSONObject(data);
                                if (DocUtils.hasValue(object, "msg")) {
                                    msg = object.getString("msg");
                                }
                                if (DocUtils.hasValue(object, "code")) {
                                    code = object.getString("code");
                                }
                                if (msg.equals("ok") && code.equals("0")) {
                                    Intent intent =new Intent(ProductShowActivity.this,ShopCartActivity.class);
                                    startActivity(intent);
                                } else {
                                    ToastUtil.showLong(ProductShowActivity.this, "加入购物车失败!");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}



