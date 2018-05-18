package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.AddressBean;
import com.real.doctor.realdoc.model.RecieverAddressListBean;
import com.real.doctor.realdoc.model.RecieverBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressListActivity extends AppCompatActivity {

    public final static int ADD_EVENT_REQUEST_CODE = 1;
    public final static int EDIT_EVENT_REQUEST_CODE = 2;

    @BindView(R.id.addresses_list_rv)
    RecyclerView addresses_list_rv;

    AddressListAsapter mAddressListAsapter;

    // Data
    RecieverBean mRecieverBean;
    List<RecieverAddressListBean> mRecieverAddressListBean;
    AddressBean mAddressBean;

    int itemPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        initData();

        addresses_list_rv.setLayoutManager(new LinearLayoutManager(this));
        mAddressListAsapter = new AddressListAsapter(R.layout.address_list_view, mRecieverAddressListBean);

        // add space between rv items
        RecyclerView.ItemDecoration dividerItemDecoration = new AddressListActivity.VerticalSpaceItemDecoration(this, Color.parseColor("#FFFFFF"), 10.0f);
        addresses_list_rv.addItemDecoration(dividerItemDecoration);

        // set footer to all notifications recyclerview
        LinearLayout linearLayout = new LinearLayout(this);
        View footer = getLayoutInflater().inflate(R.layout.address_list_add_address_footer, linearLayout);
        mAddressListAsapter.addFooterView(footer);


        addresses_list_rv.setAdapter(mAddressListAsapter);

        Button addAddress = footer.findViewById(R.id.addAddress);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Add address", "click");

                Intent intent = new Intent(AddressListActivity.this, EditAddressActivity.class);
                intent.putExtra("requestCode", ADD_EVENT_REQUEST_CODE);
                startActivityForResult(intent, ADD_EVENT_REQUEST_CODE);
            }
        });


        // Edit and Delete buttons listeners
        mAddressListAsapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                itemPosition = position;

                switch (view.getId()){
                    case R.id.change_address:
                        Intent intent = new Intent(AddressListActivity.this, EditAddressActivity.class);
                        // get the current data and send it to the edit address screen
                        intent.putExtra("requestCode", EDIT_EVENT_REQUEST_CODE);
                        intent.putExtra("name", mRecieverAddressListBean.get(position).getName());
                        intent.putExtra("phone", mRecieverAddressListBean.get(position).getPhone());
                        intent.putExtra("provinceCityDistrict", mRecieverAddressListBean.get(position).getAddress().getProvinceCityDistrict());
                        intent.putExtra("streetDetails", mRecieverAddressListBean.get(position).getAddress().getStreetDetails());

                        startActivityForResult(intent, EDIT_EVENT_REQUEST_CODE);
                        break;
                    case R.id.delete_address:
                        mRecieverAddressListBean.remove(position);
                        mAddressListAsapter.notifyDataSetChanged();
                        break;
                }
            }
        });

    }

    public void initData() {
        //mRecieverBean = new RecieverBean();

        mRecieverAddressListBean = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mAddressBean = new AddressBean();
            mAddressBean.setProvinceCityDistrict("Province City District" + i);
            mAddressBean.setStreetDetails("Street details" + i);
            //mAddressBean.setFullAddress("Province", "City", "District", "Street details" + i);

            RecieverAddressListBean bean = new RecieverAddressListBean();
            bean.setName("Name " + i);
            bean.setPhone("Phone " + i);
            bean.setAddress(mAddressBean);

            mRecieverAddressListBean.add(bean);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            String name = data.getStringExtra("recieverName");
            String phone = data.getStringExtra("recieverPhone");
            String provinceCityDistrictStr = data.getStringExtra("provinceCityDistrictStr");
            String street = data.getStringExtra("recieverStreet");

            if(requestCode == EDIT_EVENT_REQUEST_CODE) {
                // update data after editing
                mRecieverAddressListBean.get(itemPosition).setName(name);
                mRecieverAddressListBean.get(itemPosition).setPhone(phone);
                mRecieverAddressListBean.get(itemPosition).getAddress().setProvinceCityDistrict(provinceCityDistrictStr);
                mRecieverAddressListBean.get(itemPosition).getAddress().setStreetDetails(street);

            }
            else if(requestCode == ADD_EVENT_REQUEST_CODE) {
                mAddressBean = new AddressBean();
                mAddressBean.setProvinceCityDistrict(provinceCityDistrictStr);
                mAddressBean.setStreetDetails(street);

                RecieverAddressListBean bean = new RecieverAddressListBean();
                bean.setName(name);
                bean.setPhone(phone);
                bean.setAddress(mAddressBean);

                mRecieverAddressListBean.add(bean);
            }

        }



            mAddressListAsapter.notifyDataSetChanged();


    }

    class AddressListAsapter extends BaseQuickAdapter<RecieverAddressListBean, BaseViewHolder>{


        public AddressListAsapter(int layoutResId, @Nullable List<RecieverAddressListBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, RecieverAddressListBean item) {
            helper.setText(R.id.product_reciever_name, item.getName())
                    .setText(R.id.product_reciever_phone, item.getPhone())
                    .setText(R.id.product_reciever_full_address, item.getAddress().getProvinceCityDistrict() + item.getAddress().getStreetDetails())
                    .addOnClickListener(R.id.change_address)
                    .addOnClickListener(R.id.delete_address);
        }
    }





    /** Utility functions **/
    // adding top margin between recyclerview items
    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private Paint mPaint;

        public VerticalSpaceItemDecoration(Context context, int color, float heightDp) {

            mPaint = new Paint();
            mPaint.setColor(color);
            final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    heightDp, context.getResources().getDisplayMetrics());
            mPaint.setStrokeWidth(thickness);
        }


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            // we want to retrieve the position in the list
            final int position = params.getViewAdapterPosition();

            // and add a separator to any view but the last one
            if (position < state.getItemCount()) {
                outRect.set(0, (int) mPaint.getStrokeWidth(), 0, 0); // left, top, right, bottom
            } else {
                outRect.setEmpty(); // 0, 0, 0, 0
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            // we set the stroke width before, so as to correctly draw the line we have to offset by width / 2
            final int offset = (int) (mPaint.getStrokeWidth() / 2);

            // this will iterate over every visible view
            for (int i = 0; i < parent.getChildCount(); i++) {
                // get the view
                final View view = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                // get the position
                final int position = params.getViewAdapterPosition();

                // and finally draw the separator
                if (position < state.getItemCount()) {
                    c.drawLine(view.getLeft(), view.getTop() - offset, view.getRight(), view.getTop() - offset, mPaint);
                }
            }
        }


    }

}
