package com.real.doctor.realdoc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.DynamicTimeFormat;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/20.
 */

public class RegistrationActivity extends CheckPermissionsActivity  implements OnFilterDoneListener,OnLoadmoreListener,OnRefreshListener {

    @BindView(R.id.right_title)
    TextView right_title;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.dropMenu)
    DropDownMenu dropDownMenu;
    @BindView(R.id.lv_list)
    ListView lv_list;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    public FilterBean filterBean;
    private DropMenuAdapter dropMenuAdapter;
    private ClassicsHeader mClassicsHeader;
    private String[] titleList;//标题
    public ArrayList<HospitalBean> hospitalBeanArrayList=new ArrayList<HospitalBean>();
    private PageModel<HospitalBean> baseModel = new PageModel<HospitalBean>();
    public HospitalAdapter hospitalAdapter;
    public int pageNum=1;
    public int pageSize=10;
    public String hospitalLevel="";
    public String sortstr="";
    public String cityName="";
    public String searchstr="";
    public double latitude;
    public double longitude;
    //定位相关类
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    public final static int REGISTRATION_EVENT_REQUEST_CODE = 2;
    public final static int REGISTRATION_AREA_EVENT_REQUEST_CODE = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        initData();
        initLocation();
        startLocation();
        //getData();
    }
    public void initData(){
        titleList = new String[]{"默认排序","医院等级"};
        filterBean=new FilterBean();
        filterBean.setSortList(DataUtil.sortBeans);
        filterBean.setHospitalLevelBeans(DataUtil.hospitalLevelBeans);
        initFilterDropDownView();
        int deta = new Random().nextInt(7 * 24 * 60 * 60 * 1000);
        mClassicsHeader = (ClassicsHeader) refreshLayout.getRefreshHeader();
        mClassicsHeader.setLastUpdateTime(new Date(System.currentTimeMillis() - deta));
        mClassicsHeader.setTimeFormat(new SimpleDateFormat("更新于 MM-dd HH:mm", Locale.CHINA));
        mClassicsHeader.setTimeFormat(new DynamicTimeFormat("更新于 %s"));
        ClassicsFooter footer=(ClassicsFooter) refreshLayout.getRefreshFooter();
        refreshLayout.setOnLoadmoreListener(this);
        refreshLayout.setOnRefreshListener(this);
        hospitalAdapter=new HospitalAdapter(RegistrationActivity.this,hospitalBeanArrayList);
        lv_list.setAdapter(hospitalAdapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               HospitalBean bean= (HospitalBean) parent.getAdapter().getItem(position);
               Intent intent =new Intent(RegistrationActivity.this,DeptListActivity.class);
               intent.putExtra("hospitalId",bean.hospitalId);
               startActivity(intent);
            }
        });
    }
    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
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
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
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
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    right_title.setText(location.getProvince());
                } else {

                }
                getData();
            }
        }
    };
    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
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
    public void initEvents(){
    }
    @OnClick({R.id.finish_back,R.id.home_search,R.id.right_title})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.finish_back:
                RegistrationActivity.this.finish();
                break;
            case R.id.home_search:
                Intent intent = new Intent(RegistrationActivity.this, SearchHistoryActivity.class);
                intent.putExtra("requestCode", REGISTRATION_EVENT_REQUEST_CODE);;
                startActivity(intent);
                break;
            case R.id.right_title:
                Intent intentArea = new Intent(RegistrationActivity.this, AppointmentAddressActivity.class);
                intentArea.putExtra("requestCode", REGISTRATION_AREA_EVENT_REQUEST_CODE);;
                startActivityForResult(intentArea,REGISTRATION_AREA_EVENT_REQUEST_CODE);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            String province = data.getStringExtra("province");
            String city= data.getStringExtra("city");

            if(requestCode == REGISTRATION_EVENT_REQUEST_CODE) {
                // 地区回传
                cityName=city;
                pageNum=1;
                hospitalBeanArrayList.clear();
                getData();
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
                sortstr=item.SortId;
                hospitalBeanArrayList.clear();
                pageNum=1;
                getData();
            }
        });
        //等级回调
        dropMenuAdapter.setOnLevelCallbackListener(new DropMenuAdapter.OnLevelCallbackListener() {
            @Override
            public void onLevelCallbackListener(HospitalLevelBean item) {
                hospitalLevel=item.LevelName;
                hospitalBeanArrayList.clear();
                pageNum=1;
                getData();
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
        if(pageSize*pageNum>hospitalBeanArrayList.size()){
            ToastUtil.show(RegistrationActivity.this,"已经是最后一页", Toast.LENGTH_SHORT);
            refreshlayout.finishLoadmore();
            return;
        }
        pageNum++;
        getData();
        refreshlayout.finishLoadmore();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        pageNum=1;
        hospitalBeanArrayList.clear();
        getData();
        refreshLayout.finishRefresh();
    }
    private void getData() {
        HashMap<String,Object> params=new HashMap<String,Object>();
        params.put("pageNum",pageNum);
        params.put("pageSize",pageSize);
        params.put("hospitalLevel",hospitalLevel);
        params.put("sortstr",sortstr);
        params.put("cityName",cityName);
        params.put("searchstr",searchstr);
        HttpRequestClient.getInstance(RegistrationActivity.this).createBaseApi().get("guahao/hospital"
                , params, new BaseObserver<ResponseBody>(RegistrationActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                       ToastUtil.showLong(RegistrationActivity.this, e.getMessage());
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
                                    JSONObject jsonObject=object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    baseModel = localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<PageModel<HospitalBean>>() {
                                            }.getType());
                                    hospitalBeanArrayList.addAll(baseModel.list);
                                    hospitalAdapter.notifyDataSetChanged();
                                } else {
                                    ToastUtil.showLong(RegistrationActivity.this, msg.toString().trim());
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
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
