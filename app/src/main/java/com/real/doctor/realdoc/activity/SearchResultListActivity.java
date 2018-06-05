package com.real.doctor.realdoc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpNetUtil;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.DropDownMenuForResult;

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

public class SearchResultListActivity extends BaseActivity implements OnFilterDoneListener,ExpertAdapter.MyClickListener{
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
    //@BindView(R.id.dropMenu)
    DropDownMenuForResult dropDownMenu;
    public String hospitalLevel="";
    public String sortstr="";
    public String cityName="";
    public String positional="";
    public String searchstr="";
    public FilterBean filterBean;
    public String userId;
    private String[] titleList;//标题
    private DropMenuAdapterForResult dropMenuAdapter;
    public ArrayList<HospitalBean> hospitalBeans=new ArrayList<HospitalBean>();
    HospitalAdapter adapter;
    public ArrayList<ExpertBean> expertBeans=new ArrayList<ExpertBean>();
    ExpertAdapter expertAdapter;


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
        userId=(String) SPUtils.get(SearchResultListActivity.this, Constants.USER_KEY,"");
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(SearchResultListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
        dropDownMenu=findViewById(R.id.dropMenu);
        searchstr= getIntent().getStringExtra("searchKey");
        init();
        searchHospital();
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case R.id.finish_back:
                SearchResultListActivity.this.finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {

    }
    public void init() {
        page_title.setText("搜索结果");
        titleList = new String[]{"排序","筛选"};
        filterBean=new FilterBean();
        filterBean.setSortList(DataUtil.sortBeans);
        filterBean.setHospitalLevelBeans(DataUtil.hospitalLevelBeans);
        filterBean.setExpertPostionalBeans(DataUtil.expertPostionalBeans);
        initFilterDropDownView();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_hospital:// first
                        hosptial_list.setVisibility(View.VISIBLE);
                        expert_list.setVisibility(View.GONE);
                        break;
                    case R.id.rb_expert:// 第二个
                        hosptial_list.setVisibility(View.GONE);
                        expert_list.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });
        adapter=new HospitalAdapter(SearchResultListActivity.this,hospitalBeans);
        hosptial_list.setAdapter(adapter);
        hosptial_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HospitalBean bean= (HospitalBean) adapterView.getAdapter().getItem(i);
                Intent intent =new Intent(SearchResultListActivity.this,DeptListActivity.class);
                intent.putExtra("hospitalId",bean.hospitalId);
                startActivity(intent);
            }
        });
        expertAdapter=new ExpertAdapter(SearchResultListActivity.this,expertBeans,this);
        expert_list.setAdapter(expertAdapter);
    }
/**
 * 显示
 */
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
                sortstr=item.SortId;

            }
        });
        //等级回调
        dropMenuAdapter.setOnMultiCallbackListener(new DropMenuAdapterForResult.OnMultiCallbackListener() {

            @Override
            public void onMultiCallbackListener(HospitalLevelBean bean, SortBean bean2, ExpertPostionalBean bean3) {
                if(bean==null||bean.LevelName.equals("不限")){
                    hospitalLevel="";
                }else{
                    hospitalLevel=bean.LevelName;
                }
                if(bean2==null||bean2.sortName.equals("不限")){
                    sortstr="";
                }else{
                    sortstr=bean2.sortName;
                }
                if(bean3==null||bean3.postional.equals("不限")){
                    positional="";
                }else{
                    positional=bean3.postional;
                }
                searchHospital();
            }
        });


    }

    private void searchHospital() {
        HashMap<String,Object> params=new HashMap<String,Object>();
        params.put("hospitalLevel",hospitalLevel);
        params.put("sortstr",sortstr);
        params.put("cityName",cityName);
        params.put("positional",positional);
        params.put("searchstr",searchstr);
        HttpRequestClient.getInstance(SearchResultListActivity.this).createBaseApi().get("guahao/search"
                , params, new BaseObserver<ResponseBody>(SearchResultListActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(SearchResultListActivity.this, e.getMessage());
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
                                    expertBeans.clear();
                                    hospitalBeans.clear();
                                    JSONObject jsonObject=object.getJSONObject("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    expertBeans.addAll((ArrayList<ExpertBean>)(localGson.fromJson(jsonObject.getJSONArray("doctorList").toString(),new TypeToken<ArrayList<ExpertBean>>(){}.getType()
                                    )));
                                    expertAdapter.notifyDataSetChanged();
                                    hospitalBeans.addAll((ArrayList<HospitalBean>)(localGson.fromJson(jsonObject.getJSONArray("hospitalList").toString(),new TypeToken<ArrayList<HospitalBean>>(){}.getType())));
                                    adapter.notifyDataSetChanged();
                                    if(rb_hospital.isChecked()){
                                        hosptial_list.setVisibility(View.VISIBLE);
                                        expert_list.setVisibility(View.GONE);
                                    }else if(rb_expert.isChecked())
                                    {
                                        hosptial_list.setVisibility(View.GONE);
                                        expert_list.setVisibility(View.VISIBLE);
                                    }
                                } else {
//
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
        ExpertBean bean= (ExpertBean)v.getTag();
        orderExpert(bean);
    }
    public void orderExpert(ExpertBean bean){
        JSONObject object=new JSONObject();
        try {
            object.put("deptId",bean.deptId);
            object.put("doctorCode",bean.doctorCode);
            object.put("hospitalDoctorDutyId",bean.hospitalDoctorDutyId);
            object.put("hospitalId",bean.hospitalId);
            object.put("orderDay",bean.dutyDtime);
            object.put("userId",userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String token = (String) SPUtils.get(SearchResultListActivity.this, "token", "");
        Map<String, String> header = null;
        if (EmptyUtils.isNotEmpty(token)) {
            header = new HashMap<String, String>();
            header.put("Authorization", token);
        } else {
            ToastUtil.showLong(SearchResultListActivity.this, "请确定您的账户已登录!");
            return;
        }
        HttpRequestClient client= HttpRequestClient.getInstance(SearchResultListActivity.this, HttpNetUtil.BASE_URL,header);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), object.toString());
        client.createBaseApi().json("guahao/fastorder/"
                , body, new BaseObserver<ResponseBody>(SearchResultListActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onError(Throwable e) {
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
                                    ToastUtil.showLong(SearchResultListActivity.this, "预约成功!");
                                } else {
                                    ToastUtil.showLong(SearchResultListActivity.this, "预约失败!");
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
