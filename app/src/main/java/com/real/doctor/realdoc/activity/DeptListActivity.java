package com.real.doctor.realdoc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.adapter.LeftAdapter;
import com.real.doctor.realdoc.adapter.RightAdapter;
import com.real.doctor.realdoc.base.BaseActivity;
import com.real.doctor.realdoc.model.DeptBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.ScreenUtil;
import com.real.doctor.realdoc.view.DocContentDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/4/23.
 */

public class DeptListActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.lv_left)
    ListView lListView;
    @BindView(R.id.lv_right)
    RecyclerView rListView;
    @BindView(R.id.finish_back)
    ImageView finish_back;
    @BindView(R.id.page_title)
    TextView page_title;
    ArrayList<DeptBean> arrayList = new ArrayList<DeptBean>();
    LeftAdapter leftAdapter;
    RightAdapter rightAdapter;
    private String hospitalId;
    private boolean isFirst = true;
//    private DocContentDialog dialog;
    //该医生无法预约
//    private static final int REQUEST_CODE_NO_EXPERT = 0x100;

    @Override
    public int getLayoutId() {
        return R.layout.activity_dept_list;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        //加上沉浸式状态栏高度
        int statusHeight = ScreenUtil.getStatusHeight(DeptListActivity.this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
            lp.topMargin = statusHeight;
            titleBar.setLayoutParams(lp);
        }
    }

    @Override
    public void initData() {
        rListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        hospitalId = getIntent().getStringExtra("hospitalId");
        page_title.setText("预约科室");
        leftAdapter = new LeftAdapter(DeptListActivity.this, arrayList);
        lListView.setAdapter(leftAdapter);
        lListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                leftAdapter.setSelectedPosition(position);
                leftAdapter.notifyDataSetInvalidated();
                DeptBean bean = leftAdapter.getItem(position);
                List<DeptBean> deptList = new ArrayList();
                deptList.addAll(bean.deptList);
                rightAdapter = new RightAdapter(R.layout.dept_item_layout, deptList);
                rListView.setAdapter(rightAdapter);
                initEvent();
            }
        });
    }

    @Override
    public void initEvent() {
        if (EmptyUtils.isNotEmpty(rightAdapter)) {
            rightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    DeptBean dBean = (DeptBean) adapter.getItem(position);
                    Intent intent = new Intent(DeptListActivity.this, OrderExpertActivity.class);
                    intent.putExtra("hospitalId", hospitalId);
                    intent.putExtra("deptCode", dBean.deptCode);
                    startActivity(intent);
//                    startActivityForResult(intent, REQUEST_CODE_NO_EXPERT);
                }
            });
        }
    }

    @Override
    @OnClick({R.id.finish_back})
    public void widgetClick(View v) {
        switch (v.getId()) {
            case R.id.finish_back:
                finish();
                break;
        }
    }

    @Override
    public void doBusiness(Context mContext) {
        getData();
    }

    public void getData() {
        HashMap<String, String> param = new HashMap<String, String>();
        HttpRequestClient.getInstance(DeptListActivity.this).createBaseApi().get("guahao/hospital/deptCategory/"
                , param, new BaseObserver<ResponseBody>(DeptListActivity.this) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
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
                                    JSONArray jsonObject = object.getJSONArray("data");
                                    Gson localGson = new GsonBuilder()
                                            .create();
                                    arrayList.addAll((ArrayList<DeptBean>) localGson.fromJson(jsonObject.toString(),
                                            new TypeToken<ArrayList<DeptBean>>() {
                                            }.getType()));
                                    if (isFirst) {
                                        leftAdapter.setSelectedPosition(0);
                                        final DeptBean bean = (DeptBean) leftAdapter.getItem(0);
                                        List<DeptBean> list = new ArrayList<>();
                                        list.addAll(bean.deptList);
                                        rightAdapter = new RightAdapter(R.layout.dept_item_layout, list);
                                        rListView.setAdapter(rightAdapter);
                                        isFirst = false;
                                    }
                                    leftAdapter.notifyDataSetChanged();
                                    initEvent();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_NO_EXPERT) {
//            //如果医生无法预约,弹出对话框提示
//            dialog = new DocContentDialog(DeptListActivity.this, "该医生无法预约!").builder()
//                    .setCancelable(false)
//                    .setCanceledOnTouchOutside(true)
//                    .setConfirmBtn(new DocContentDialog.ConfirmListener() {
//                        @Override
//                        public void onConfirmClick() {
//                            dialog.dismiss();
//                        }
//                    }).show();
//        }
//    }
}
