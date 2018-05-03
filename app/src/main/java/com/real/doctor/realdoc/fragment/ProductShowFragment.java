package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.BrandAdapter;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.BrandListView;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ProductShowFragment extends BaseFragment {
    public CategoryBean bean;
    @BindView(R.id.lv_brands)
    BrandListView brandListView;
    @BindView(R.id.lv_products)
    ListView listView;
    public BrandAdapter brandAdapter;
    public ProductAdapter productAdapter;
    public static ProductShowFragment newInstance(CategoryBean bean) {
        bean=bean;
        return new ProductShowFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void doBusiness(final Context mContext) {
        brandAdapter =new BrandAdapter(mContext,bean.brands);
        brandListView.setAdapter(brandAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                final int location=position;
                brandAdapter.setSelectedPosition(position);
                brandAdapter.notifyDataSetInvalidated();
                final BrandBean bean=	(BrandBean) brandAdapter.getItem(position);
                productAdapter=new ProductAdapter(mContext, bean.productList);
                listView.setAdapter(productAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int position, long arg3) {
                        ProductInfo  product= bean.productList.get(position);
                        ToastUtil.show(mContext,product.getName(), Toast.LENGTH_SHORT);
                    }
                });

            }
        });

    }

    @Override
    public void widgetClick(View v) {

    }
}
