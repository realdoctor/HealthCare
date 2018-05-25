package com.real.doctor.realdoc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.API.NotificationsPOJO.Data;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.DropMenuAdapter;
import com.real.doctor.realdoc.adapter.DropMenuAdapterForResult;
import com.real.doctor.realdoc.adapter.ExpertAdapter;
import com.real.doctor.realdoc.adapter.FragPagerAdapter;
import com.real.doctor.realdoc.adapter.HospitalAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.fragment.OrderExpertByDateFragment;
import com.real.doctor.realdoc.fragment.OrderExpertByNameFragment;
import com.real.doctor.realdoc.model.ExpertBean;
import com.real.doctor.realdoc.model.ExpertPostionalBean;
import com.real.doctor.realdoc.model.FilterBean;
import com.real.doctor.realdoc.model.HospitalBean;
import com.real.doctor.realdoc.model.HospitalLevelBean;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.SortBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DataUtil;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.OnFilterDoneListener;
import com.real.doctor.realdoc.util.ToastUtil;
import com.real.doctor.realdoc.view.CustomViewPager;
import com.real.doctor.realdoc.view.DropDownMenu;
import com.real.doctor.realdoc.view.DropDownMenuForResult;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class SearchResultActivity extends Activity implements OnFilterDoneListener,ExpertAdapter.MyClickListener {
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
    @BindView(R.id.dropMenu)
    DropDownMenuForResult dropDownMenu;
    public String hospitalLevel="";
    public String sortstr="";
    public String cityName="";
    public String positional="";
    public String searchstr="";
    public FilterBean filterBean;
    private String[] titleList;//标题
    private DropMenuAdapterForResult dropMenuAdapter;
    public ArrayList<HospitalBean> hospitalBeans=new ArrayList<HospitalBean>();
    HospitalAdapter adapter;
    public ArrayList<ExpertBean> expertBeans=new ArrayList<ExpertBean>();
    ExpertAdapter expertAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
        searchstr= getIntent().getStringExtra("searchKey");
        initData();
        searchHospital();
    }
    @OnClick(R.id.finish_back)
    void back(){
        finish();
    }
    public void initData() {
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
        adapter=new HospitalAdapter(SearchResultActivity.this,hospitalBeans);
        hosptial_list.setAdapter(adapter);
        expertAdapter=new ExpertAdapter(SearchResultActivity.this,expertBeans,this);
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
        HttpRequestClient.getInstance(SearchResultActivity.this).createBaseApi().get("guahao/search"
                , params, new BaseObserver<ResponseBody>(SearchResultActivity.this) {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(SearchResultActivity.this, e.getMessage());
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

    }
}
