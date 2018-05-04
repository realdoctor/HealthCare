package com.real.doctor.realdoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.ProductShowActivity;
import com.real.doctor.realdoc.activity.SearchActivity;
import com.real.doctor.realdoc.adapter.BrandAdapter;
import com.real.doctor.realdoc.adapter.ProductAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.BrandBean;
import com.real.doctor.realdoc.model.CategoryBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.BrandListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/4/18.
 */

public class ProductShowFragment extends BaseFragment {
    private Unbinder unbinder;
    public CategoryBean bean;
    @BindView(R.id.lv_brands)
    BrandListView brandListView;
    @BindView(R.id.lv_products)
    ListView listView;
    public BrandAdapter brandAdapter;
    public ProductAdapter productAdapter;
    public static ProductShowFragment newInstance(CategoryBean bean) {
        ProductShowFragment fragment=new ProductShowFragment();
        Bundle bundel=new Bundle();
        bundel.putSerializable("model",bean);
        fragment.setArguments(bundel);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void doBusiness(final Context mContext) {
        if(getArguments()!=null) {
            bean=(CategoryBean)getArguments().get("model");
            brandAdapter = new BrandAdapter(mContext, bean.brands);
            brandListView.setAdapter(brandAdapter);
            brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    final int location = position;
                    brandAdapter.setSelectedPosition(position);
                    brandAdapter.notifyDataSetInvalidated();
                    final BrandBean bean = (BrandBean) brandAdapter.getItem(position);
                    productAdapter = new ProductAdapter(mContext, bean.productList);
                    listView.setAdapter(productAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {
                            ProductInfo product = bean.productList.get(position);
                            Intent intent = new Intent(getActivity(), ProductShowActivity.class);
                            intent.putExtra("model",product);
                            startActivity(intent);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            getActivity().finish();

                        }
                    });

                }
            });
            selectDefault();
        }
    }

    @Override
    public void widgetClick(View v) {

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    //默认选中
    private void selectDefault(){
        final int location=0;
        brandAdapter.setSelectedPosition(0);
        brandAdapter.notifyDataSetInvalidated();
        final BrandBean breadBean=	(BrandBean) brandAdapter.getItem(0);
        productAdapter=new ProductAdapter(getContext(), breadBean.productList);
        listView.setAdapter(productAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                ProductInfo product= breadBean.productList.get(position);
                Intent intent = new Intent(getActivity(), ProductShowActivity.class);
                intent.putExtra("model",product);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                getActivity().finish();
            }
        });
    }
}
