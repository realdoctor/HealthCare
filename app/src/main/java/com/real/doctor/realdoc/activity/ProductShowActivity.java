package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ProductItemAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.widget.refreshrecyclerview.base.adapter.BaseRecyclerViewAdapter;
import com.real.doctor.realdoc.widget.refreshrecyclerview.pulltorefresh.PullToRefreshRecyclerView;
import com.youth.banner.Banner;
//import com.youth.banner.Banner;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ProductShowActivity extends BaseActivity  {

    @BindView(R.id.banner_id)
    Banner banner;
    @BindView(R.id.tv_pName)
    TextView tv_pName;
    @BindView(R.id.tv_pCompay)
    TextView tv_pCompay;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_buy)
    TextView goToBuy;
    @BindView(R.id.tv_incart)
    TextView addCart;
    @BindView(R.id.tv_cart)
    TextView cart;
    private ProductInfo bean;
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
        bean=(ProductInfo) getIntent().getSerializableExtra("model");
        banner.setBannerStyle(Banner.CIRCLE_INDICATOR_TITLE);
        banner.setIndicatorGravity(Banner.CENTER);
        banner.isAutoPlay(true) ;
        banner.setImages(new String[]{bean.getImageUrl()});
        tv_pName.setText(bean.getName());
        tv_price.setText("￥"+bean.getPrice());
    }

    @Override
    public void initEvent() {

    }


    @Override
    @OnClick({R.id.tv_buy,R.id.tv_cart,R.id.tv_incart})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.tv_buy:
                Intent intent =new Intent(ProductShowActivity.this,PayActivity.class);
                startActivity(intent);
                ProductShowActivity.this.finish();
                break;
            case R.id.tv_incart:
                addToCart();
                break;
            case R.id.tv_cart:
                Intent intentCart =new Intent(ProductShowActivity.this,ShopCartActivity.class);
                startActivity(intentCart);
                ProductShowActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    //添加到购物车
    public void addToCart(){

    }
}



