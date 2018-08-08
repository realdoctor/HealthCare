package com.real.doctor.realdoc.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DropMenuAdapterForResult;
import com.real.doctor.realdoc.adapter.ExpertAdapter;
import com.real.doctor.realdoc.adapter.HospitalAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.FilterBean;
import com.real.doctor.realdoc.model.HospitalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DistanceUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DropDownMenuForResult;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
 * Created by Administrator on 2018/4/23.
 */

public class SearchResultListActivity extends BaseActivity implements OnFilterDoneListener, ExpertAdapter.MyClickListener, OnLoadmoreListener, OnRefreshListener {
    @BindView(R.id.right_title)
    TextView right_title;
    @BindView(R.id.rg)
    RadioGroup radioGroup;
    @BindView(R.id.rb_hospital)
    RadioButton rb_hospital;
    @BindView(R.id.rb_expert)
    RadioButton rb_expert;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    @BindView(R.id.hosptial_list)
    ListView hosptial_list;
    @BindView(R.id.expert_list)
    ListView expert_list;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.refreshLayout)

    SmartRefreshLayout refreshLayout;
    //@BindView(R.id.dropMenu)
    DropDownMenuForResult dropDownMenu;
    public String hospitalLevel = "";
    public String sortstr = "";
    public String cityName = "";
    public String positional = "";
    public String searchstr = "";
    public FilterBean filterBean;
    public int pageNum = 1;
    public int pageSize = 10;
    public int pageNum2 = 1;
    public String tag = "1";
    public String userId;
    private String[] titleList;//标题
    private DropMenuAdapterForResult dropMenuAdapter;
    private PageModel<HospitalBean> baseModel = new PageModel<HospitalBean>();
    public ArrayList<HospitalBean> hospitalBeans = new ArrayList<HospitalBean>();
    HospitalAdapter adapter;
    private PageModel<ExpertBean> baseModel1 = new PageModel<ExpertBean>();
    public ArrayList<ExpertBean> expertBeans = new ArrayList<ExpertBean>();
    //定位相关类
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public final static int REGISTRATION_AREA_EVENT_REQUEST_CODE = 5;
    ExpertAdapter expertAdapter;
    public static double latitude;
    public static double longitude;
    private Dialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_result;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        userId = (String) SPUtils.get(SearchResultListActivity.this, Constants.USER_KEY, "");
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SearchResultListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        dropDownMenu = findViewById(R.id.dropMenu);
        searchstr = getIntent().getStringExtra("searchKey");
        right_title.setVisibility(View.VISIBLE);
        right_title.setText("定位中");
        mProgressDialog = DocUtils.getProgressDialog(SearchResultListActivity.this, "正在加载数据....");
        init();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back, R.id.right_title})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                goBackBtn();
                break;
            case R.id.right_title:
                Intent intentArea = new Intent(SearchResultListActivity.this, AppointmentAddressActivity.class);
                intentArea.putExtra("requestCode", REGISTRATION_AREA_EVENT_REQUEST_CODE);
                startActivityForResult(intentArea, REGISTRATION_AREA_EVENT_REQUEST_CODE);
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    String city = location.getCity();
                    right_title.setText(city);
                    cityName = city;
                } else {

                }
                searchHospital(pageNum, pageSize);
            }
        }
    };

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    public void init() {
        page_title.setText("搜索结果");
        titleList = new String[]{"排序", "筛选"};
        filterBean = new FilterBean();
        filterBean.setSortList(DataUtil.sortBeans);
        filterBean.setHospitalLevelBeans(DataUtil.hospitalLevelBeans);
        filterBean.setExpertPostionalBeans(DataUtil.expertPostionalBeans);
        initFilterDropDownView();
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_hospital:// first
                        hosptial_list.setVisibility(View.VISIBLE);
                        expert_list.setVisibility(View.GONE);
                        tag = "1";
                        break;
                    case R.id.rb_expert:// 第二个
                        hosptial_list.setVisibility(View.GONE);
                        expert_list.setVisibility(View.VISIBLE);
                        tag = "2";
                        break;
                    default:
                        break;
                }
            }
        });
        adapter = new HospitalAdapter(SearchResultListActivity.this, hospitalBeans);
        hosptial_list.setAdapter(adapter);
        hosptial_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HospitalBean bean = (HospitalBean) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(SearchResultListActivity.this, DeptListActivity.class);
                intent.putExtra("hospitalId", bean.hospitalId);
                startActivity(intent);
            }
        });
        expertAdapter = new ExpertAdapter(SearchResultListActivity.this, expertBeans, this);
        expert_list.setAdapter(expertAdapter);
        //判断当前是否是6.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
        } else {
            initLocation();
            startLocation();
        }
    }

    /**
     * 筛选框 初始化+获取列表数据+筛选条件监听
     */
    private void initFilterDropDownView() {
        //绑定数据源
        dropMenuAdapter = new DropMenuAdapterForResult(this, titleList, this);
        dropMenuAdapter.setFilterBean(filterBean);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
        // 排序回调
        dropMenuAdapter.setOnSortCallbackListener(new DropMenuAdapterForResult.OnSortCallbackListener() {
            @Override
            public void onSortCallbackListener(SortBean item) {
                sortstr = item.SortId;

            }
        });
        //等级回调
        dropMenuAdapter.setOnMultiCallbackListener(new DropMenuAdapterForResult.OnMultiCallbackListener() {

            @Override
            public void onMultiCallbackListener(HospitalLevelBean bean, SortBean bean2, ExpertPostionalBean bean3) {
                if (bean == null || bean.LevelName.equals("不限")) {
                    hospitalLevel = "";
                } else {
                    hospitalLevel = bean.LevelName;
                }
                if (bean2 == null || bean2.sortName.equals("不限")) {
                    sortstr = "";
                } else {
                    sortstr = bean2.sortName;
                }
                if (bean3 == null || bean3.postional.equals("不限")) {
                    positional = "";
                } else {
                    positional = bean3.postional;
                }
                if (tag.equals("1")) {
                    searchHospital(pageNum, pageSize);
                } else {
                    searchHospital(pageNum2, pageSize);
                }
            }
        });


    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (tag.equals("1")) {
            if (pageSize * pageNum > hospitalBeans.size()) {
                ToastUtil.show(SearchResultListActivity.this, "已经是最后一页", Toast.LENGTH_SHORT);
                refreshlayout.finishLoadmore();
                return;
            }
            pageNum++;
            searchHospital(pageNum, pageSize);
            refreshlayout.finishLoadmore();
        } else {
            if (pageSize * pageNum2 > expertBeans.size()) {
                ToastUtil.show(SearchResultListActivity.this, "已经是最后一页", Toast.LENGTH_SHORT);
                refreshlayout.finishLoadmore();
                return;
            }
            pageNum2++;
            searchHospital(pageNum2, pageSize);
            refreshlayout.finishLoadmore();
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (tag.equals("1")) {
            pageNum = 1;
            hospitalBeans.clear();
            searchHospital(pageNum, pageSize);
            refreshLayout.finishRefresh();
        } else {
            pageNum2 = 1;
            expertBeans.clear();
            searchHospital(pageNum2, pageSize);
            refreshLayout.finishRefresh();
        }

    }

    private void searchHospital(int pageNum, int pageSize) {
        mProgressDialog.show();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("tag", tag);
        params.put("sortstr", sortstr);
        params.put("cityName", cityName);
        params.put("positional", positional);
        params.put("searchstr", searchstr);
        HttpRequestClient.getNotInstance(SearchResultListActivity.this, HttpNetUtil.SEARCH_URL, null).createBaseApi().get("guahao/search"
                , params, new BaseObserver<ResponseBody>(SearchResultListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        ToastUtil.showLong(SearchResultListActivity.this, "查询失败!");
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
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
                                    expertBeans.clear();
                                    hospitalBeans.clear();
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel1 = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<ExpertBean>>() {
                                            }.getType());

                                    expertBeans.addAll(baseModel1.list);
                                    expertAdapter.notifyDataSetChanged();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<HospitalBean>>() {
                                            }.getType());
                                    if (baseModel.list.size() == 0) {
                                        ToastUtil.showLong(SearchResultListActivity.this, "查询结果为空!");
                                    }
                                    for (HospitalBean bean : baseModel.list) {
                                        bean.distance = DistanceUtil.getDistance(SearchResultListActivity.latitude, SearchResultListActivity.longitude, Double.parseDouble(bean.lat), Double.parseDouble(bean.lng));
                                        hospitalBeans.add(bean);
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (rb_hospital.isChecked()) {
                                        hosptial_list.setVisibility(View.VISIBLE);
                                        expert_list.setVisibility(View.GONE);
                                    } else if (rb_expert.isChecked()) {
                                        hosptial_list.setVisibility(View.GONE);
                                        expert_list.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    ToastUtil.showLong(SearchResultListActivity.this, "查询失败!");
                                }
                                mProgressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 筛选器title的变化
     * <p>
     * 点击到选中的item，自动收回
     *
     * @param position
     * @param positionTitle
     * @param urlValue
     */
    @Override
    public void onFilterDone(int position, String positionTitle, String urlValue) {
        //数据显示到筛选标题中
        dropDownMenu.setPositionIndicatorText(position, positionTitle);
        dropDownMenu.close();
    }

    @Override
    public void clickListener(View v) {
        ExpertBean bean = (ExpertBean) v.getTag();
        orderExpert(bean);
    }

    public void orderExpert(ExpertBean bean) {
        mProgressDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("deptId", bean.deptId);
            object.put("doctorCode", bean.doctorCode);
            object.put("hospitalDoctorDutyId", bean.hospitalDoctorDutyId);
            object.put("hospitalId", bean.hospitalId);
            object.put("orderDay", bean.dutyDtime);
            object.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequestClient client = HttpRequestClient.getInstance(SearchResultListActivity.this, HttpNetUtil.SEARCH_URL);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("guahao/fastorder/"
                , body, new BaseObserver<ResponseBody>(SearchResultListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Log.d(TAG, e.getMessage());
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
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
                                    ToastUtil.showLong(SearchResultListActivity.this, "预约成功!");
                                } else {
                                    ToastUtil.showLong(SearchResultListActivity.this, "预约失败!");
                                }
                                mProgressDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                });


    }

    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
                initLocation();
                startLocation();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String province = data.getStringExtra("province");
            String city = data.getStringExtra("city");

            if (requestCode == REGISTRATION_AREA_EVENT_REQUEST_CODE) {
                // 地区回传
                cityName = city;
                right_title.setText(cityName);
                if (tag.equals("1")) {
                    pageNum = 1;
                    hospitalBeans.clear();
                    searchHospital(pageNum, pageSize);
                    refreshLayout.finishRefresh();
                } else {
                    pageNum2 = 1;
                    expertBeans.clear();
                    searchHospital(pageNum2, pageSize);
                    refreshLayout.finishRefresh();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBackBtn();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBackBtn() {
        //将地址还给baseUrl
        HttpRequestClient client = HttpRequestClient.getNotInstance(SearchResultListActivity.this, HttpNetUtil.BASE_URL, null);
        if (EmptyUtils.isNotEmpty(client)) {
            SearchResultListActivity.this.finish();
        }
    }
}
