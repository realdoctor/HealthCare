package com.real.doctor.realdoc.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.ShopcartExpandableListViewAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.GroupInfo;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.model.ProductInfo;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ShopCartActivity extends BaseActivity implements ShopcartExpandableListViewAdapter.CheckInterface, ShopcartExpandableListViewAdapter.ModifyCountInterface, View.OnClickListener {

    @BindView(R.id.exListView)
    ExpandableListView exListView;
    @BindView(R.id.all_chekbox)
    CheckBox cbCheck;
    @BindView(R.id.tv_total_price)
    TextView tvTotalPrice;
    @BindView(R.id.tv_delete)
    TextView tvDelete;
    @BindView(R.id.tv_go_to_pay)
    TextView tvGoToPay;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.finish_back)
    ImageView finishBack;
    @BindView(R.id.title_bar)
    RelativeLayout topTitle;

    private Context context;
    private double totalPrice = 0.00;// 购买的商品总价
    private int totalCount = 0;// 购买的商品总数量

    private ShopcartExpandableListViewAdapter selva;
    private List<GroupInfo> groups = new ArrayList<GroupInfo>();// 组元素数据列表
    private Map<String, List<ProductBean>> children = new HashMap<String, List<ProductBean>>();// 子元素数据列表
    String userId;
    //默认店铺
    public GroupInfo groupInfo;
    //默认店铺id
    public static String gourpId = "0001";

    @Override
    public int getLayoutId() {
        return R.layout.activity_shopping_cart;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ShopCartActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
            lp.topMargin = statusHeight;
            topTitle.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(ShopCartActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) topTitle.getLayoutParams();
            lp.topMargin = statusHeight;
            topTitle.setLayoutParams(lp);
        }
        pageTitle.setText("购物车");
        context = this;
        userId = (String) SPUtils.get(ShopCartActivity.this, Constants.USER_KEY, "");
        groups.add(new GroupInfo(gourpId, "自营店铺"));
        // virtualData();
    }

    @Override
    public void initEvent() {
        selva = new ShopcartExpandableListViewAdapter(groups, children, ShopCartActivity.this);
        selva.setCheckInterface(ShopCartActivity.this);// 关键步骤1,设置复选框接口
        selva.setModifyCountInterface(ShopCartActivity.this);// 关键步骤2,设置数量增减接口
        exListView.setAdapter(selva);
        finishBack.setOnClickListener(this);
        cbCheck.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvGoToPay.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCartData();
    }

    @Override
    public void widgetClick(View v) {
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View v) {
        AlertDialog alert;
        switch (v.getId()) {
            case R.id.all_chekbox:
                doCheckAll();
                break;
            case R.id.finish_back:
                ShopCartActivity.this.finish();
                break;
            case R.id.tv_go_to_pay:
                if (totalCount == 0) {
                    Toast.makeText(context, "请选择要支付的商品", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(context).create();
                alert.setTitle("操作提示");
                alert.setMessage("总计:\n" + totalCount + "种商品\n" + totalPrice + "元");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShopCartActivity.this, PayActivity.class);
                        intent.putExtra("totalPrice", String.valueOf(totalPrice));
                        ArrayList<ProductBean> list = new ArrayList<ProductBean>();
                        for (ProductBean bean : children.get(gourpId)) {
                            if (bean.isChoosed()) {
                                list.add(bean);
                            }
                        }
                        intent.putExtra("goodsList", list);
                        startActivity(intent);
                    }
                });
                alert.show();
                break;
            case R.id.tv_delete:
                if (totalCount == 0) {
                    Toast.makeText(context, "请选择要移除的商品", Toast.LENGTH_LONG).show();
                    return;
                }
                alert = new AlertDialog.Builder(context).create();
                alert.setTitle("操作提示");
                alert.setMessage("您确定要将这些商品从购物车中移除吗？");
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doDelete();
                    }
                });
                alert.show();
                break;
        }
    }

    /**
     * 删除操作<br>
     * 1.不要边遍历边删除，容易出现数组越界的情况<br>
     * 2.现将要删除的对象放进相应的列表容器中，待遍历完后，以removeAll的方式进行删除
     */
    protected void doDelete() {
        List<GroupInfo> toBeDeleteGroups = new ArrayList<GroupInfo>();// 待删除的组元素列表
        //封装要删除的购物车商品
        final JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < groups.size(); i++) {
            GroupInfo group = groups.get(i);
            if (group.isChoosed()) {
                toBeDeleteGroups.add(group);
            }
            List<ProductBean> toBeDeleteProducts = new ArrayList<ProductBean>();// 待删除的子元素列表
            List<ProductBean> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                if (childs.get(j).isChoosed()) {
                    toBeDeleteProducts.add(childs.get(j));
                }
            }
            childs.removeAll(toBeDeleteProducts);
            for (int k = 0; k < toBeDeleteProducts.size(); k++) {
                jsonArray.put(toBeDeleteProducts.get(k).getGoodsShopcarId());
            }

        }
        deleteCartIds(jsonArray);
    }

    private void deleteCartIds(JSONArray array) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ids", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        HttpRequestClient.getInstance(ShopCartActivity.this).createBaseApi().json("cart/deleteCartItem/"
                , body, new BaseObserver<ResponseBody>(ShopCartActivity.this) {

                    @Override

                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    ToastUtil.showLong(ShopCartActivity.this, "删除成功!");
                                    getCartData();
                                } else {
                                    ToastUtil.showLong(ShopCartActivity.this, "删除失败!");
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

    @Override
    public void doIncrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        ProductBean product = (ProductBean) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getNum();
        currentCount++;
        product.setNum(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void doDecrease(int groupPosition, int childPosition, View showCountView, boolean isChecked) {
        ProductBean product = (ProductBean) selva.getChild(groupPosition, childPosition);
        int currentCount = product.getNum();
        if (currentCount == 1)
            return;
        currentCount--;
        product.setNum(currentCount);
        ((TextView) showCountView).setText(currentCount + "");
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkGroup(int groupPosition, boolean isChecked) {
        GroupInfo group = groups.get(groupPosition);
        List<ProductBean> childs = children.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            childs.get(i).setChoosed(isChecked);
        }
        if (isAllCheck())
            cbCheck.setChecked(true);
        else
            cbCheck.setChecked(false);
        selva.notifyDataSetChanged();
        calculate();
    }

    @Override
    public void checkChild(int groupPosition, int childPosiTion, boolean isChecked) {
        boolean allChildSameState = true;// 判断改组下面的所有子元素是否是同一种状态
        GroupInfo group = groups.get(groupPosition);
        List<ProductBean> childs = children.get(group.getId());
        for (int i = 0; i < childs.size(); i++) {
            if (childs.get(i).isChoosed() != isChecked) {
                allChildSameState = false;
                break;
            }
        }
        if (allChildSameState) {
            group.setChoosed(isChecked);// 如果所有子元素状态相同，那么对应的组元素被设为这种统一状态
        } else {
            group.setChoosed(false);// 否则，组元素一律设置为未选中状态
        }

        if (isAllCheck())
            cbCheck.setChecked(true);
        else
            cbCheck.setChecked(false);
        selva.notifyDataSetChanged();
        calculate();
    }

    private boolean isAllCheck() {

        for (GroupInfo group : groups) {
            if (!group.isChoosed())
                return false;
        }

        return true;
    }

    /**
     * 全选与反选
     */
    private void doCheckAll() {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setChoosed(cbCheck.isChecked());
            GroupInfo group = groups.get(i);
            List<ProductBean> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                childs.get(j).setChoosed(cbCheck.isChecked());
            }
        }
        selva.notifyDataSetChanged();
        calculate();
    }

    /**
     * 统计操作<br>
     * 1.先清空全局计数器<br>
     * 2.遍历所有子元素，只要是被选中状态的，就进行相关的计算操作<br>
     * 3.给底部的textView进行数据填充
     */
    private void calculate() {
        totalCount = 0;
        totalPrice = 0.00;
        for (int i = 0; i < groups.size(); i++) {
            GroupInfo group = groups.get(i);
            List<ProductBean> childs = children.get(group.getId());
            for (int j = 0; j < childs.size(); j++) {
                ProductBean product = childs.get(j);
                if (product.isChoosed()) {
                    totalCount++;
                    totalPrice += product.getCost() * product.getNum();
                }
            }
        }
        tvTotalPrice.setText("￥" + totalPrice);
        tvGoToPay.setText("去支付(" + totalCount + ")");
    }

    private void getCartData() {
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", userId);
        HttpRequestClient.getInstance(ShopCartActivity.this).createBaseApi().get("cart/"
                , param, new BaseObserver<ResponseBody>(ShopCartActivity.this) {

                    @Override

                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
//                        ToastUtil.showLong(RegisterActivity.this, e.getMessage());
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
                                    JSONArray array = object.getJSONArray("data");
                                    List<ProductBean> products = new Gson().fromJson(array.toString(), new TypeToken<ArrayList<ProductBean>>() {
                                    }.getType());
                                    if (products.size() == 0) {
                                        groups.clear();
                                        children.remove(gourpId);
                                    } else {
                                        children.remove(gourpId);
                                        children.put(gourpId, products);// 将组元素的一个唯一值，这里取Id，作为子元素List的Key
                                    }
                                    selva.notifyDataSetChanged();
                                    for (int k = 0; k < selva.getGroupCount(); k++) {
                                        exListView.expandGroup(k);// 关键步骤3,初始化时，将ExpandableListView以展开的方式呈现
                                    }
                                    calculate();
                                } else {

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