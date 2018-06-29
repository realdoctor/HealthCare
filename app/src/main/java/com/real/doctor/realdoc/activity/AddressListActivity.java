package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.greendao.table.RecieverAddressListManager;
import com.real.doctor.realdoc.model.AddressBean;
import com.real.doctor.realdoc.model.RecieverAddressListBean;
import com.real.doctor.realdoc.model.RecieverBean;
import com.real.doctor.realdoc.util.ScreenUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressListActivity extends AppCompatActivity {

    public final static int ADD_EVENT_REQUEST_CODE = 1;
    public final static int EDIT_EVENT_REQUEST_CODE = 2;

    @BindView(R.id.addresses_list_rv)
    RecyclerView addresses_list_rv;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.page_title)
    TextView page_title;
    public RecieverAddressListManager addressInstance;
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
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(AddressListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        page_title.setText("我的收货地址");
        addressInstance= RecieverAddressListManager.getInstance(AddressListActivity.this);
        mRecieverAddressListBean = new ArrayList<>();
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
        mAddressListAsapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent mIntent = new Intent();
                mIntent.putExtra("item",mRecieverAddressListBean.get(position));
                setResult(RESULT_OK,mIntent);
                AddressListActivity.this.finish();
                // 设置结果，并进行传送

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
                        intent.putExtra("provinceCityDistrict", mRecieverAddressListBean.get(position).addressStr);
                        intent.putExtra("streetDetails", mRecieverAddressListBean.get(position).daddress);
                        intent.putExtra("model",mRecieverAddressListBean.get(position));
                        startActivity(intent);
                        break;
                    case R.id.delete_address:
                        addressInstance.deleteBean(mRecieverAddressListBean.get(position).id);
                        mRecieverAddressListBean.clear();
                        mRecieverAddressListBean.addAll(addressInstance.queryBeanList(AddressListActivity.this));
                        mAddressListAsapter.notifyDataSetChanged();
                        break;
                }
            }
        });

    }
    class AddressListAsapter extends BaseQuickAdapter<RecieverAddressListBean, BaseViewHolder>{


        public AddressListAsapter(int layoutResId, @Nullable List<RecieverAddressListBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, RecieverAddressListBean item) {
            helper.setText(R.id.product_reciever_name, item.getName())
                    .setText(R.id.product_reciever_phone, item.getPhone())
                    .setText(R.id.product_reciever_full_address, item.addressStr+item.daddress)
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

    @Override
    protected void onResume() {
        super.onResume();
        mRecieverAddressListBean.clear();
        mRecieverAddressListBean.addAll(addressInstance.queryBeanList(AddressListActivity.this));
        mAddressListAsapter.notifyDataSetChanged();
    }
}
