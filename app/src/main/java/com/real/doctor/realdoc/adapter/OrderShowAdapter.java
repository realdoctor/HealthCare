package com.real.doctor.realdoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.model.OrderDetailModel;
import com.real.doctor.realdoc.model.OrderModel;

import java.util.List;

/**
 * Created by ZFT on 2018/6/1.
 */

public class OrderShowAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<OrderModel> paretntItem;

    public OrderShowAdapter(Context context, List<OrderModel> parentItem){
        this.context = context;
        this.paretntItem = parentItem;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getGroupView(int parentPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        OrderModel friendGroupItem = paretntItem.get(parentPosition);
        View view;
        OrderShowAdapter.ParentViewHolder parentViewHolder;
        if (convertView == null){
            view = inflater.inflate(R.layout.order_parent_layout,parent,false);
            parentViewHolder = new OrderShowAdapter.ParentViewHolder();
            parentViewHolder.tv_goodsOrderId = (TextView)view.findViewById(R.id.tv_goodsOrderId);
            //parentViewHolder.iv_expand = (ImageView)view.findViewById(R.id.id_friendgroup_item_parent_iv_expand);
            parentViewHolder.tv_tradeStatus = (TextView)view.findViewById(R.id.tv_tradeStatus);
            view.setTag(parentViewHolder);
        }else{
            view  = convertView;
            parentViewHolder = (OrderShowAdapter.ParentViewHolder)view.getTag();
        }

        //判断isExpanded就可以控制是按下还是关闭，同时更换图片
//        if (isExpanded){
//            parentViewHolder.iv_expand.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_expanded));
//        }else{
//            parentViewHolder.iv_expand.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unexpanded));
//        }

        parentViewHolder.tv_goodsOrderId.setText("订单编号："+friendGroupItem.goodsOrderId);
        parentViewHolder.tv_tradeStatus.setText("订单总额："+friendGroupItem.payPrice);
        return view;
    }

    @Override
    public View getChildView(int parentPosition, int childPosition, boolean b, View convertView, ViewGroup parent) {
        OrderDetailModel friendItem = paretntItem.get(parentPosition).orderList.get(childPosition);
        View view;
        OrderShowAdapter.ChildViewHolder childViewHolder;
        if(convertView == null){
            view = inflater.inflate(R.layout.order_child_item,parent,false);
            childViewHolder = new OrderShowAdapter.ChildViewHolder();
            childViewHolder.product_detail_img = (ImageView)view.findViewById(R.id.product_detail_img);
            childViewHolder.product_detail_title = (TextView)view.findViewById(R.id.product_detail_title);
            childViewHolder.product_price = (TextView)view.findViewById(R.id.product_price);
            view.setTag(childViewHolder);
        }else{
            view = convertView;
            childViewHolder = (OrderShowAdapter.ChildViewHolder)view.getTag();
        }
        Glide.with(context)
                .load(friendItem.smallPic)
                .into(childViewHolder.product_detail_img);
        childViewHolder.product_detail_title.setText(friendItem.name);
        childViewHolder.product_price.setText(friendItem.goodsPrice);
        return view;
    }

    public class ParentViewHolder{
        TextView tv_goodsOrderId;
        TextView tv_tradeStatus;
    }

    public class ChildViewHolder{
        ImageView product_detail_img;
        TextView product_detail_title;
        TextView product_price;
    }

    @Override
    public int getGroupCount() {
        return paretntItem.size();
    }

    @Override
    public int getChildrenCount(int parentPosition) {
        return paretntItem.get(parentPosition).orderList.size();
    }

    @Override
    public Object getGroup(int parengPosition) {
        return paretntItem.get(parengPosition);
    }

    @Override
    public Object getChild(int parentPosition, int childPosition) {
        return paretntItem.get(parentPosition).orderList.get(childPosition);
    }

    @Override
    public long getGroupId(int parentPosition) {
        return parentPosition;
    }

    @Override
    public long getChildId(int parentPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
