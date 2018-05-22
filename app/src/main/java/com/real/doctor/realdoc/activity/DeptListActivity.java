package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.LeftAdapter;
import com.real.doctor.realdoc.adapter.RightAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DeptBean;
import com.real.doctor.realdoc.model.PageModel;
import com.real.doctor.realdoc.model.ProductBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONArray;
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

public class DeptListActivity extends BaseActivity {
    @BindView(R.id.lv_left)
    ListView lListView;
    @BindView(R.id.lv_right)
    ListView rListView;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;


    ArrayList<DeptBean> arrayList=new ArrayList<DeptBean>();
    LeftAdapter leftAdapter;
    RightAdapter rightAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_dept_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initData() {
        page_title.setText("预约科室");
        leftAdapter= new LeftAdapter(DeptListActivity.this,arrayList);
        lListView.setAdapter(leftAdapter);
        lListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                        long arg3) {
                    // TODO Auto-generated method stub
                    final int location = position;
                    leftAdapter.setSelectedPosition(position);
                    leftAdapter.notifyDataSetInvalidated();
                    final DeptBean bean = (DeptBean) leftAdapter.getItem(position);
                    rightAdapter = new RightAdapter(DeptListActivity.this, bean.deptList);
                    rListView.setAdapter(rightAdapter);
                    rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int position, long arg3) {
                            DeptBean dBean = bean.deptList.get(position);

                        }
                    });

                }
            });
    }

    @Override
    public void initEvent() {

    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()){
            case  R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        getData();
    }
    public void getData(){
        HashMap<String,String> param=new HashMap<String,String>();
        HttpRequestClient.getInstance(DeptListActivity.this).createBaseApi().get("guahao/hospital/deptCategory/"
                , param, new BaseObserver<ResponseBody>(DeptListActivity.this) {

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
                                    JSONArray jsonObject=object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    arrayList.addAll((ArrayList<DeptBean>)localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<DeptBean>>() {
                                            }.getType()));
                                    leftAdapter.notifyDataSetChanged();

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
