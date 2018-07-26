package com.real.doctor.realdoc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.real.doctor.realdoc.adapter.DropMenuAdapter;
import com.real.doctor.realdoc.adapter.HospitalAdapter;
import com.real.doctor.realdoc.model.FilterBean;
import com.real.doctor.realdoc.model.HospitalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DistanceUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DropDownMenu;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class RegistrationsActivity extends CheckPermissionsActivity implements OnFilterDoneListener, OnLoadmoreListener, OnRefreshListener {

    @BindView(R.id.right_title)
    TextView right_title;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    //@BindView(R.id.dropMenu)
    DropDownMenu dropDownMenu;
    @BindView(R.id.lv_list)
    ListView lv_list;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.top_bar)
    RelativeLayout titleBar;
    public FilterBean filterBean;
    private DropMenuAdapter dropMenuAdapter;
    private ClassicsHeader mClassicsHeader;
    private String[] titleList;//标题
    public ArrayList<HospitalBean> hospitalBeanArrayList = new ArrayList<HospitalBean>();
    private PageModel<HospitalBean> baseModel = new PageModel<HospitalBean>();
    public HospitalAdapter hospitalAdapter;
    public int pageNum = 1;
    public int pageSize = 10;
    public String hospitalLevel = "";
    public String sortstr = "";
    public String cityName = "";
    public String searchstr = "";
    public static double latitude;
    public static double longitude;
    //定位相关类
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public final static int REGISTRATION_EVENT_REQUEST_CODE = 2;
    public final static int REGISTRATION_AREA_EVENT_REQUEST_CODE = 4;
    private Dialog mProgressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_registration;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        dropDownMenu = (DropDownMenu) this.findViewById(R.id.dropMenu);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(RegistrationsActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        mProgressDialog = DocUtils.getProgressDialog(RegistrationsActivity.this, "正在加载数据....");
        init();
        initLocation();
        startLocation();
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    public void init() {
        titleList = new String[]{"默认排序", "医院等级"};
        filterBean = new FilterBean();
        filterBean.setSortList(DataUtil.sortBeans);
        filterBean.setHospitalLevelBeans(DataUtil.hospitalLevelBeans);
        initFilterDropDownView();
        mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
        ClassicsFooter footer = (ClassicsFooter) refreshLayout.getRefreshFooter();
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        hospitalAdapter = new HospitalAdapter(RegistrationsActivity.this, hospitalBeanArrayList);
        lv_list.setAdapter(hospitalAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HospitalBean bean = (HospitalBean) parent.getAdapter().getItem(position);
                Intent intent = new Intent(RegistrationsActivity.this, DeptListActivity.class);
                intent.putExtra("hospitalId", bean.hospitalId);
                startActivity(intent);
            }
        });
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
                getData();
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
        mProgressDialog.show();
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

    public void initEvents() {
    }

    @OnClick({R.id.finish_back, R.id.home_search, R.id.right_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish_back:
                RegistrationsActivity.this.finish();
                break;
            case R.id.home_search:
                Intent intent = new Intent(RegistrationsActivity.this, SearchHistoryListActivity.class);
                intent.putExtra("requestCode", REGISTRATION_EVENT_REQUEST_CODE);
                startActivity(intent);
                break;
            case R.id.right_title:
                Intent intentArea = new Intent(RegistrationsActivity.this, AppointmentAddressActivity.class);
                intentArea.putExtra("requestCode", REGISTRATION_AREA_EVENT_REQUEST_CODE);
                startActivityForResult(intentArea, REGISTRATION_AREA_EVENT_REQUEST_CODE);
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
                pageNum = 1;
                hospitalBeanArrayList.clear();
                mProgressDialog.show();
                getData();
                refreshLayout.finishRefresh();
            }
        }


    }
/**
 * 显示
 */
    /**
     * 筛选框 初始化+获取列表数据+筛选条件监听
     */
    private void initFilterDropDownView() {
        //绑定数据源
        dropMenuAdapter = new DropMenuAdapter(this, titleList, this);
        dropMenuAdapter.setFilterBean(filterBean);
        dropDownMenu.setMenuAdapter(dropMenuAdapter);
        // 排序回调
        dropMenuAdapter.setOnSortCallbackListener(new DropMenuAdapter.OnSortCallbackListener() {
            @Override
            public void onSortCallbackListener(SortBean item) {
                if (item == null || item.sortName.equals("离我最近")) {
                    sortstr = "sortByDistance";
                    Collections.sort(hospitalBeanArrayList, comparator);
                    hospitalAdapter.notifyDataSetChanged();

                } else {
                    sortstr = item.SortId;
                    hospitalBeanArrayList.clear();
                    pageNum = 1;
                    mProgressDialog.show();
                    getData();
                    refreshLayout.finishRefresh();
                }

            }
        });
        //等级回调
        dropMenuAdapter.setOnLevelCallbackListener(new DropMenuAdapter.OnLevelCallbackListener() {
            @Override
            public void onLevelCallbackListener(HospitalLevelBean item) {
                if (item == null || item.LevelName.equals("不限")) {
                    hospitalLevel = "";
                } else {
                    hospitalLevel = item.LevelName;
                    hospitalBeanArrayList.clear();
                    pageNum = 1;
                    mProgressDialog.show();
                    getData();
                    refreshLayout.finishRefresh();
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
    public void onLoadmore(RefreshLayout refreshlayout) {
        if (pageSize * pageNum > hospitalBeanArrayList.size()) {
            ToastUtil.show(RegistrationsActivity.this, "已经是最后一页", Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        mProgressDialog.show();
        getData();
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum = 1;
        hospitalBeanArrayList.clear();
        mProgressDialog.show();
        getData();
        refreshLayout.finishRefresh();
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pageNum", pageNum);
        params.put("pageSize", pageSize);
        params.put("hospitalLevel", hospitalLevel);
        params.put("sortstr", sortstr);
        params.put("cityName", cityName);
        params.put("searchstr", searchstr);
        HttpRequestClient.getInstance(RegistrationsActivity.this).createBaseApi().get("guahao/hospital"
                , params, new BaseObserver<ResponseBody>(RegistrationsActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        ToastUtil.showLong(RegistrationsActivity.this, e.getMessage());
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
                                    JSONObject jsonObject = object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<HospitalBean>>() {
                                            }.getType());

                                    for (HospitalBean bean : baseModel.list) {
                                        bean.distance = DistanceUtil.getDistance(RegistrationsActivity.latitude, RegistrationsActivity.longitude, Double.parseDouble(bean.lat), Double.parseDouble(bean.lng));
                                        hospitalBeanArrayList.add(bean);
                                    }

                                    if (sortstr.equals("sortByDistance")) {
                                        Collections.sort(hospitalBeanArrayList, comparator);
                                    }
                                    hospitalAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.showLong(RegistrationsActivity.this, msg.toString().trim());
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
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

    public Comparator<HospitalBean> comparator = new Comparator<HospitalBean>() {
        @Override
        public int compare(HospitalBean o1, HospitalBean o2) {
            double i = o1.distance - o2.distance;
            return (int) i;
        }
    };
}