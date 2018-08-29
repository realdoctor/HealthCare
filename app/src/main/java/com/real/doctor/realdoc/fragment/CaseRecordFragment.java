package com.real.doctor.realdoc.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.real.doctor.realdoc.R;
import com.real.doctor.realdoc.activity.CaseListActivity;
import com.real.doctor.realdoc.adapter.CaseControlAdapter;
import com.real.doctor.realdoc.base.BaseFragment;
import com.real.doctor.realdoc.model.PatientBean;
import com.real.doctor.realdoc.rxjavaretrofit.entity.BaseObserver;
import com.real.doctor.realdoc.rxjavaretrofit.http.HttpRequestClient;
import com.real.doctor.realdoc.util.Constants;
import com.real.doctor.realdoc.util.DocUtils;
import com.real.doctor.realdoc.util.EmptyUtils;
import com.real.doctor.realdoc.util.GsonUtil;
import com.real.doctor.realdoc.util.SPUtils;
import com.real.doctor.realdoc.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class CaseRecordFragment extends BaseFragment {

    @BindView(R.id.search_patient)
    EditText searchPatient;
    @BindView(R.id.my_patient_rv)
    RecyclerView myPatientRv;
    private Unbinder unbinder;
    CaseControlAdapter caseControlAdapter;
    private List<PatientBean> patientList;
    private static int pageNum = 1;
    private String mUserId;

    @Override
    public int getLayoutId() {
        return R.layout.case_record_frag;
    }

    @Override
    public void initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        initData();
    }


    public void initData() {
        //添加权限
        PackageManager p = getActivity().getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                p.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", "com.real.doctor.realdoc"));
        if (!permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
        patientList = new ArrayList<>();
        //添加自定义分割线
        myPatientRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.disease_divider));
        myPatientRv.addItemDecoration(divider);
        caseControlAdapter = new CaseControlAdapter(R.layout.case_control_item, patientList);
        mUserId = (String) SPUtils.get(getActivity(), Constants.USER_KEY, "");
        initCaseControl("");
    }

    private void initCaseControl(String searchStr) {
        HashMap<String, String> param = new HashMap<>();
        param.put("pageNum", String.valueOf(pageNum));
        param.put("roleId", "1");
        if (EmptyUtils.isNotEmpty(searchStr)) {
            param.put("searchStr", searchStr);
        }
        param.put("pageSize", "10");
        param.put("status", "1");
        param.put("userId", mUserId);
        HttpRequestClient.getInstance(getActivity()).createBaseApi().get("askQuestion/reply/patientList"
                , param, new BaseObserver<ResponseBody>(getActivity()) {
                    protected Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showLong(getActivity(), "获取患者管理列表失败!");
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
                                    if (DocUtils.hasValue(object, "data")) {
                                        JSONObject obj = object.getJSONObject("data");
                                        if (DocUtils.hasValue(obj, "list")) {
                                            patientList = GsonUtil.GsonToList(obj.getJSONArray("list").toString(), PatientBean.class);
                                            caseControlAdapter = new CaseControlAdapter(R.layout.case_control_item, patientList);
                                            myPatientRv.setAdapter(caseControlAdapter);
                                            initEvent();
                                        }
                                    }
                                } else {
                                    ToastUtil.showLong(getActivity(), "获取患者管理列表失败!");
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

    public void initEvent() {
        caseControlAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), CaseListActivity.class);
                PatientBean patientBean = (PatientBean) adapter.getItem(position);
                intent.putExtra("realName", patientBean.getDoctorRealName());
                intent.putExtra("src", patientBean.getSrc());
                intent.putExtra("title", patientBean.getTitle());
                intent.putExtra("questionId", patientBean.getQuestionId());
                intent.putExtra("question", patientBean.getQuestion());
                intent.putExtra("patientRecordId", patientBean.getPatientRecordId());
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        searchPatient.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String search = searchPatient.getText().toString().trim();
                    if (EmptyUtils.isNotEmpty(search)) {
                        initCaseControl(search);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
